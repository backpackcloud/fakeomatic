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

import com.backpackcloud.fakeomatic.sampler.impl.sample.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.function.Supplier;

/**
 * Represents a sample of data.
 *
 * @author Marcelo Guimarães
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(name = CachedSample.TYPE, value = CachedSample.class),
  @JsonSubTypes.Type(name = CharSample.TYPE, value = CharSample.class),
  @JsonSubTypes.Type(name = JoiningSample.TYPE, value = JoiningSample.class),
  @JsonSubTypes.Type(name = TimestampSample.TYPE, value = TimestampSample.class),
  @JsonSubTypes.Type(name = ExpressionSample.TYPE, value = ExpressionSample.class),
  @JsonSubTypes.Type(name = JsonValueSample.TYPE, value = JsonValueSample.class),
  @JsonSubTypes.Type(name = ListSample.TYPE, value = ListSample.class),
  @JsonSubTypes.Type(name = RangeSample.TYPE, value = RangeSample.class),
  @JsonSubTypes.Type(name = SourceSample.TYPE, value = SourceSample.class),
  @JsonSubTypes.Type(name = UuidSample.TYPE, value = UuidSample.class),
  @JsonSubTypes.Type(name = WeightedSample.TYPE, value = WeightedSample.class),
  @JsonSubTypes.Type(name = TemplateSample.TYPE, value = TemplateSample.class),
  @JsonSubTypes.Type(name = HttpSample.TYPE, value = HttpSample.class),
  @JsonSubTypes.Type(name = NowSample.TYPE, value = NowSample.class),
  @JsonSubTypes.Type(name = FormatterSample.TYPE, value = FormatterSample.class),
  @JsonSubTypes.Type(name = SequenceSample.TYPE, value = SequenceSample.class),
  @JsonSubTypes.Type(name = StorageSample.TYPE, value = StorageSample.class),
  @JsonSubTypes.Type(name = JsonSample.TYPE, value = JsonSample.class)
})
@RegisterForReflection
@FunctionalInterface
public interface Sample<E> extends Supplier<E> {

  /**
   * Gets one sample of the data this object holds.
   *
   * @return a sample data. Might be random.
   */
  E get();

  default String type() {
    return getClass().getSimpleName();
  }

  /**
   * Creates a new Sample that always returns the given value.
   *
   * @param value the value to return
   * @return a new Sample
   */
  static <E> Sample<E> of(E value) {
    return () -> value;
  }

}
