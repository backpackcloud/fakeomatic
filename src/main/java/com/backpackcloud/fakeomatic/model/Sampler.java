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

package com.backpackcloud.fakeomatic.model;

import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.io.SerialBitter;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Defines a component that can produce fake data.
 * <p>
 * This is the root object of the templates.
 *
 * @author Marcelo Guimarães
 * @see Sample
 */
public class Sampler {

  private final RandomGenerator random;
  private final Map<String, Sample> samples;
  private final Map<Character, String> placeholders;

  @JsonCreator
  public Sampler(@JacksonInject RandomGenerator random,
                 @JsonProperty("samples") Map<String, Sample> samples,
                 @JsonProperty("placeholders") Map<Character, String> placeholders) {
    this.random = random;
    this.samples = Optional.ofNullable(samples).orElseGet(HashMap::new);
    this.placeholders = Optional.ofNullable(placeholders).orElseGet(HashMap::new);
  }

  /**
   * Gets all the samples that this sampler holds.
   *
   * @return a map containing the samples.
   */
  public Map<String, Sample> samples() {
    return new HashMap<>(this.samples);
  }

  public Map<Character, String> placeholders() {
    return new HashMap<>(this.placeholders);
  }

  /**
   * Returns the Sample data associated with the given name.
   *
   * @param sampleName the key for locating the Sample object.
   * @return the Sample object associated with the given name.
   */
  public <E> Sample<E> sample(String sampleName) {
    return () -> {
      if (!samples.containsKey(sampleName)) {
        throw new UndefinedSampleException(sampleName);
      }
      return (E) samples.get(sampleName).get();
    };
  }

  /**
   * Returns a random data from the Sample associated with the given placeholder.
   *
   * @param placeholder the placeholder to get the sample.
   * @return a random data.
   */
  public String some(char placeholder) {
    if (placeholders.containsKey(placeholder)) {
      return Objects.toString(sample(placeholders.get(placeholder)));
    }
    return String.valueOf(placeholder);
  }

  public void merge(Sampler sampler) {
    sampler.samples().forEach(this.samples::putIfAbsent);
    sampler.placeholders().forEach(this.placeholders::putIfAbsent);
  }

  public <E> E oneOf(List<? extends E> list) {
    return list.get(random.nextInt(list.size()));
  }

  public <E> E oneOf(E... args) {
    return args[random.nextInt(args.length)];
  }

  /**
   * Returns a pseudorandom data from the Sample associated with the given name.
   *
   * @param sampleName the name of the Sample.
   * @return the generated data.
   */
  public <E> E some(String sampleName) {
    Sample<Object> sample = sample(sampleName);
    return (E) sample.get();
  }

  /**
   * Generates a random expression looking for placeholders in the given expression.
   *
   * @param expression the expression to evaluate.
   * @return a random generated expression.
   * @see #some(char)
   */
  public String expression(String expression) {
    StringBuilder builder = new StringBuilder(expression.length());

    for (int i = 0; i < expression.length(); i++) {
      builder.append(some(expression.charAt(i)));
    }

    return builder.toString();
  }

  public static Sampler defaultSampler(RandomGenerator random) {
    return load(new ResourceConfiguration("META-INF/sampler.yml"), random);
  }

  public static Sampler load(Configuration configuration, RandomGenerator random) {
    SerialBitter serializer = SerialBitter.YAML();
    Sampler rootSampler = new Sampler(random, new HashMap<>(), new HashMap<>());

    serializer.addDependency(Sampler.class, rootSampler);
    serializer.addDependency(RandomGenerator.class, random);

    Sampler sampler = serializer.deserialize(
      configuration.read(),
      Sampler.class
    );

    rootSampler.merge(sampler);

    return rootSampler;
  }

}
