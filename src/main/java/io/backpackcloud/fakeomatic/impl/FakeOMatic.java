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

package io.backpackcloud.fakeomatic.impl;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.qute.TemplateData;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RegisterForReflection
@TemplateData(target = FakeData.class)
public class FakeOMatic implements FakeData {

  private final Random random;

  private final FakeData parent;

  private final Map<String, Sample> samples;
  private final Map<String, String> placeholders;

  @JsonCreator
  public FakeOMatic(@JacksonInject Random random,
                    @JacksonInject FakeData parent,
                    @JsonProperty("samples") Map<String, Sample> samples,
                    @JsonProperty("placeholders") Map<String, String> placeholders) {
    this.random = random;
    this.samples = Optional.ofNullable(samples).orElseGet(Collections::emptyMap);
    this.placeholders = Optional.ofNullable(placeholders).orElseGet(Collections::emptyMap);
    this.parent = parent;
  }

  public FakeOMatic addSample(String name, Sample sample) {
    this.samples.putIfAbsent(name, sample);
    return this;
  }

  @Override
  public Sample sample(String sampleName) {
    return Optional.ofNullable(samples.get(sampleName))
                   .orElseGet(() -> parent.sample(sampleName));
  }

  @Override
  public String random(String sampleName) {
    return sample(sampleName).get(this.random);
  }

  @Override
  public String randomFor(char placeholder) {
    return Optional.ofNullable(this.placeholders.get(String.valueOf(placeholder)))
                   .map(this::random)
                   .orElseGet(() -> this.parent.randomFor(placeholder));
  }

  @Override
  public int number(int min, int max) {
    return min + random.nextInt(max - min);
  }

}