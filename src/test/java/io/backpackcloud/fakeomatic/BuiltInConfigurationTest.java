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

package io.backpackcloud.fakeomatic;

import io.backpackcloud.fakeomatic.spi.FakeOMatic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuiltInConfigurationTest extends BaseTest {

  FakeOMatic fakeOMatic;

  @BeforeEach
  public void init() {
    fakeOMatic = createBuiltin();
  }

  @Test
  public void testPlaceholders() {
    times(1000, () -> {
      assertTrue(fakeOMatic.some('#').matches("^\\d$"));
      assertTrue(fakeOMatic.some('%').matches("^[a-z]$"));
      assertTrue(fakeOMatic.some('^').matches("^[A-Z]$"));
      assertTrue(fakeOMatic.some('*').matches("^[a-z0-9]$"));
      assertTrue(fakeOMatic.some('$').matches("^[A-Z0-9]$"));
    });
  }

  @Test
  public void testChuckNorrisSample() {
    String value = fakeOMatic.some("chuck_norris");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testTronaldDumpSample() {
    String value = fakeOMatic.some("tronald_dump");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testBusinessBullshitSample() {
    String value = fakeOMatic.some("business_bullshit");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testCommitMessageSample() {
    String value = fakeOMatic.some("commit_message");
    assertNotNull(value);
    assertFalse(value.isEmpty());
    assertFalse(value.isBlank());
  }

  @Test
  public void testErrorCauseSample() {
    times(10000, fakeOMatic.sample("error_cause"), value -> {
      assertNotNull(value);
      assertFalse(value.toString().isEmpty());
      assertFalse(value.toString().isBlank());
    });
  }

  @Test
  public void testTableFlipSample() {
    times(10000, fakeOMatic.sample("table_flip"), value -> {
      assertNotNull(value);
      assertFalse(value.toString().isEmpty());
      assertFalse(value.toString().isBlank());
    });
  }

}
