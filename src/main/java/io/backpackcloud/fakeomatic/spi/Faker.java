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
import io.backpackcloud.fakeomatic.impl.sample.ListSample;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Defines a component that can produce fake data.
 * <p>
 * This is the root object of the templates.
 *
 * @author Marcelo Guimarães
 * @see Sample
 */
public interface Faker {

  /**
   * Gets the random object being used by this FakeData.
   *
   * @return the random object.
   */
  Random random();

  /**
   * Gets all the samples that this fake data holds.
   *
   * @return a list of the samples.
   */
  List<Sample> samples();

  /**
   * Returns the Sample data associated with the given name.
   *
   * @param sampleName the key for locating the Sample object.
   * @return the Sample object associated with the given name.
   */
  Sample sample(String sampleName);

  /**
   * Returns a random data from the Sample associated with the given placeholder.
   *
   * @param placeholder the placeholder to get the sample.
   * @return a random data.
   */
  String some(char placeholder);

  /**
   * Returns a random data from the Sample associated with the given name.
   *
   * @param sampleName the name of the Sample.
   * @return a random data.
   */
  default <E> E some(String sampleName) {
    return (E) sample(sampleName).get();
  }

  default String some(String sampleName, String format) {
    return String.format(format, some(sampleName));
  }

  /**
   * Generates a random expression looking for placeholders in the given expression.
   *
   * @param expression the expression to evaluate.
   * @return a random generated expression.
   * @see #some(char)
   */
  default String expression(String expression) {
    StringBuilder builder = new StringBuilder(expression.length());

    for (int i = 0; i < expression.length(); i++) {
      builder.append(some(expression.charAt(i)));
    }

    return builder.toString();
  }

  /**
   * Picks one of the given values using the {@link #random()} object.
   *
   * @param values the values to pick
   * @return a randomly chosen value.
   */
  default <E> E oneOf(E... values) {
    return new ListSample<E>(random(), Arrays.stream(values).map(Sample::of).collect(Collectors.toList())).get();
  }

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
   * @param random    the random object to use for randomness
   * @param locations the locations of the configurations
   * @return a new FakeData
   */
  static Faker load(Random random, InputStream... locations) {
    return FakeOMaticProducer.newInstance(Arrays.asList(locations), Engine.builder().addDefaults().build(), std -> {
      std.addValue(Random.class, random);
      std.addValue(Vertx.class, Vertx.vertx());
    });
  }

  /**
   * Loads a new FakeData from the configuration stored in the given InputStreams.
   *
   * @param locations the locations of the configurations
   * @return a new FakeData
   */
  static Faker load(InputStream... locations) {
    return load(new Random(), locations);
  }

}
