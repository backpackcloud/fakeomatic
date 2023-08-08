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

package com.backpackcloud.fakeomatic.sampler;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.fakeomatic.sampler.impl.SamplerImpl;
import com.backpackcloud.serializer.Serializer;
import com.backpackcloud.trugger.util.ElementResolver;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Map;
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
@RegisterForReflection
public interface Sampler {

  /**
   * Gets all the samples that this fake data holds.
   *
   * @return a list of the samples.
   */
  Map<String, Sample> samples();

  Map<Character, String> placeholders();

  /**
   * Returns the Sample data associated with the given name.
   *
   * @param sampleName the key for locating the Sample object.
   * @return the Sample object associated with the given name.
   */
  <E> Optional<Sample<E>> sample(String sampleName);

  /**
   * Returns a random data from the Sample associated with the given placeholder.
   *
   * @param placeholder the placeholder to get the sample.
   * @return a random data.
   */
  String some(char placeholder);

  void merge(Sampler sampler);

  <E> E oneOf(List<? extends E> list);

  <E> E oneOf(E... args);

  /**
   * Returns a random data from the Sample associated with the given name.
   *
   * @param sampleName the name of the Sample.
   * @return a random data.
   */
  default <E> E some(String sampleName) {
    String[] path = sampleName.split("\\.", 2);
    if (path.length == 1) {
      Optional<Sample<Object>> sample = sample(path[0]);
      return (E) sample.map(Sample::get)
        .orElseThrow(UnbelievableException
          .because("Sample '" + sampleName + "' isn't defined."));
    }
    Optional<Sample<Object>> sample = sample(path[0]);
    return (E) sample.map(Sample::get)
      .map(ElementResolver::of)
      .map(resolver -> resolver.resolve(path[1]))
      .orElseThrow(UnbelievableException
        .because("Sample '" + sampleName + "' isn't defined."));
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

  static Sampler defaultSampler(RandomGenerator random) {
    return loadFrom(new ResourceConfiguration("META-INF/sampler.yml"), random);
  }

  static Sampler loadFrom(Configuration configuration, RandomGenerator random) {
    Serializer serializer = Serializer.yaml();
    RootSampler rootSampler = new RootSampler();

    serializer.addDependency(Sampler.class, rootSampler);
    serializer.addDependency(RandomGenerator.class, random);

    Sampler sampler = serializer.deserialize(
      configuration.read(),
      SamplerImpl.class
    );

    rootSampler.delegate = sampler;

    return sampler;
  }

  class RootSampler implements Sampler {
    Sampler delegate;

    private RootSampler() {
    }

    @Override
    public Optional<Sample> sample(String sampleName) {
      return Optional.ofNullable(() -> this.delegate.sample(sampleName).orElseThrow(UnbelievableException::new).get());
    }

    @Override
    public void merge(Sampler sampler) {
      this.delegate.merge(sampler);
    }

    @Override
    public String some(char placeholder) {
      return this.delegate.some(placeholder);
    }

    @Override
    public Object some(String sampleName) {
      return this.delegate.some(sampleName);
    }

    @Override
    public String expression(String expression) {
      return this.delegate.expression(expression);
    }

    @Override
    public <E> E oneOf(List<? extends E> list) {
      return delegate.oneOf(list);
    }

    @Override
    public <E> E oneOf(E... args) {
      return delegate.oneOf(args);
    }

    @Override
    public Map<Character, String> placeholders() {
      return this.delegate.placeholders();
    }

    @Override
    public Map<String, Sample> samples() {
      return this.delegate.samples();
    }

  }

}
