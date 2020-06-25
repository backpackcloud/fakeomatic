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

package io.backpackcloud.fakeomatic.spi.samples;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A sample that collects other samples and combine them as a unique data.
 *
 * @author Marcelo Guimarães
 */
@RegisterForReflection
public class CompositeSample implements Sample<String> {

  private final FakeData     fakeData;
  private final List<String> samples;
  private final String       separator;

  @JsonCreator
  public CompositeSample(@JacksonInject("root") FakeData fakeData,
                         @JsonProperty("samples") List<String> samples,
                         @JsonProperty("separator") String separator) {
    this.fakeData = fakeData;
    this.samples = samples;
    this.separator = Optional.ofNullable(separator).orElse("");
  }

  public List<Sample> samples() {
    return samples.stream()
                  .map(fakeData::sample)
                  .collect(Collectors.toList());
  }

  public String separator() {
    return separator;
  }

  @Override
  public String get() {
    return samples.stream()
                  .map(this.fakeData::sample)
                  .map(Sample::get)
                  .map(Object::toString)
                  .collect(Collectors.joining(this.separator));
  }

}
