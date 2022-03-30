/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.backpackcloud.fakeomatic;

import com.backpackcloud.fakeomatic.core.spi.Faker;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import com.backpackcloud.fakeomatic.impl.producer.FakeOMaticProducer;
import com.backpackcloud.fakeomatic.spi.Config;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuiltInConfigurationTest {

  Faker faker;

  public BuiltInConfigurationTest() {
    Config config = new Config() {
      @Override
      public Random random() {
        return new Random();
      }

      @Override
      public String[] configs() {
        return new String[]{"fakeomatic"};
      }

    };
    FakeOMaticProducer producer = new FakeOMaticProducer(config, Vertx.vertx(), Engine.builder().addDefaults().build());
    faker = producer.produce().faker();
  }

  @Test
  public void testPlaceholders() {
    times(1000, () -> {
      assertTrue(faker.some('#').matches("^\\d$"));
      assertTrue(faker.some('%').matches("^[a-z]$"));
      assertTrue(faker.some('^').matches("^[A-Z]$"));
      assertTrue(faker.some('*').matches("^[a-z0-9]$"));
      assertTrue(faker.some('$').matches("^[A-Z0-9]$"));
    });
  }

  @Test
  public void testChuckNorrisSample() {
    String value = faker.some("chuck_norris");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testTronaldDumpSample() {
    String value = faker.some("tronald_dump");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testBusinessBullshitSample() {
    String value = faker.some("business_bullshit");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testCommitMessageSample() {
    String value = faker.some("whatthecommit");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testErrorCauseSample() {
    times(10000, faker.sample("error_cause").get(), value -> {
      assertNotNull(value);
      assertFalse(value.toString().isEmpty());
      assertFalse(value.toString().isBlank());
    });
  }

  @Test
  public void testTableFlipSample() {
    times(10000, faker.sample("table_flip").get(), value -> {
      assertNotNull(value);
      assertFalse(value.toString().isEmpty());
      assertFalse(value.toString().isBlank());
    });
  }

  private <E> void times(int times, Sample<E> sample, Consumer<E> consumer) {
    times(times, () -> consumer.accept(sample.get()));
  }

  private void times(int times, Runnable runnable) {
    times(times, integer -> runnable.run());
  }

  private void times(int times, Consumer<Integer> consumer) {
    for (int i = 1; i <= times; i++) {
      consumer.accept(i);
    }
  }

}
