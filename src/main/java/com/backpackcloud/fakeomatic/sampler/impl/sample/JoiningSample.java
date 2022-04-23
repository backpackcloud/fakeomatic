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

package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A sample that collects other samples and combine them as a unique data.
 *
 * @author Marcelo Guimarães
 */
@RegisterForReflection
public class JoiningSample implements Sample<String> {

  public static final String TYPE = "join";

  private final List<Sample> samples;
  private final String separator;


  public JoiningSample(String separator, List<Sample> samples) {
    this.samples = samples;
    this.separator = separator;
  }

  @Override
  public String type() {
    return TYPE;
  }

  public List<Sample> samples() {
    return new ArrayList<>(samples);
  }

  public String separator() {
    return separator;
  }

  @Override
  public String get() {
    return samples.stream()
      .map(Sample::get)
      .map(Object::toString)
      .collect(Collectors.joining(this.separator));
  }

  @JsonCreator
  public static JoiningSample create(@JacksonInject Sampler sampler,
                                     @JsonProperty("samples") List<String> samples,
                                     @JsonProperty("separator") String separator) {
    return new JoiningSample(
      Optional.ofNullable(separator)
        .orElse(""),

      samples.stream()
        .map(sampler::sample)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList())
    );
  }

}
