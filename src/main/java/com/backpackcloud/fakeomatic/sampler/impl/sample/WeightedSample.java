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
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RegisterForReflection
public class WeightedSample<E> implements Sample<E> {

  public static final String TYPE = "weight";

  private final List<WeightedValue> values;
  private final List<WeightedValueDefinition<E>> definitions;

  private final Random random;

  private final int totalWeight;

  @JsonCreator
  public WeightedSample(@JacksonInject Random random,
                        @JsonProperty("values") List<WeightedValueDefinition<E>> definitions) {
    this.values = new ArrayList<>(definitions.size());
    this.definitions = definitions;
    this.random = random;
    int current = 0;
    for (WeightedValueDefinition definition : definitions) {
      this.values.add(new WeightedValue(current, definition));
      current += definition.weight();
    }
    totalWeight = current;
  }

  @Override
  public String type() {
    return TYPE;
  }

  public int totalWeight() {
    return totalWeight;
  }

  public List<WeightedValueDefinition<E>> definitions() {
    return new ArrayList<>(definitions);
  }

  @Override
  public E get() {
    int position = random.nextInt(totalWeight);
    return (E) values.stream()
      .filter(weightedValue -> weightedValue.isSelected(position))
      .map(WeightedValue::sample)
      .map(Sample::get)
      .findFirst()
      .get();
  }

  public static class WeightedValueDefinition<E> {

    private final int weight;

    private final Sample<E> sample;

    public WeightedValueDefinition(int weight, Sample<E> sample) {
      this.weight = weight;
      this.sample = sample;
    }

    public int weight() {
      return weight;
    }

    public Sample<E> sample() {
      return sample;
    }

    @JsonCreator
    public static WeightedValueDefinition create(@JsonProperty("weight") int weight,
                                                 @JsonProperty("value") Object value,
                                                 @JsonProperty("source") SampleConfiguration source) {
      return new WeightedValueDefinition(weight, value != null ? Sample.of(value) : source.sample());
    }

  }

  class WeightedValue {

    private final Sample<E> sample;
    private final int minPosition;
    private final int maxPosition;

    WeightedValue(int currentPosition, WeightedValueDefinition<E> definition) {
      this.sample = definition.sample;
      this.minPosition = currentPosition;
      this.maxPosition = currentPosition + definition.weight();
    }

    public boolean isSelected(int position) {
      return position >= minPosition && position < maxPosition;
    }

    public Sample<E> sample() {
      return sample;
    }

  }

}
