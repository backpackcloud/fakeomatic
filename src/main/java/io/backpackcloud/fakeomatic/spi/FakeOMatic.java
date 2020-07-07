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

package io.backpackcloud.fakeomatic.spi;

import io.backpackcloud.fakeomatic.impl.producer.FakeOMaticProducer;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public interface FakeOMatic extends Faker {

  Optional<Endpoint> endpoint(String name);

  /**
   * Returns the location of the default config for parsing.
   *
   * @return the InputSteam that points to the default config location.
   */
  static InputStream defaultConfigLocation() {
    return FakeOMaticProducer.defaultConfig();
  }

  /**
   * Loads a new FakeData from the configuration stored in the given InputStreams.
   *
   * @param locations the locations of the configurations
   * @return a new FakeData
   */
  static FakeOMatic load(InputStream... locations) {
    return load(new Random(), locations);
  }

  /**
   * Loads a new FakeData from the configuration stored in the given InputStreams.
   *
   * @param random    the random object to use for randomness
   * @param locations the locations of the configurations
   * @return a new FakeData
   */
  static FakeOMatic load(Random random, InputStream... locations) {
    return FakeOMaticProducer.newInstance(Arrays.asList(locations), Engine.builder().addDefaults().build(), std -> {
      std.addValue(Random.class, random);
      std.addValue(Vertx.class, Vertx.vertx());
    });
  }

}
