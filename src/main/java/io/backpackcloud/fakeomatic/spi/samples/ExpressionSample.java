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

import java.util.function.Supplier;

@RegisterForReflection
public class ExpressionSample implements Sample<String> {

  private final FakeData         fakeData;
  private final Supplier<String> expressionSupplier;

  public ExpressionSample(Supplier<String> expressionSupplier, FakeData fakeData) {
    this.expressionSupplier = expressionSupplier;
    this.fakeData = fakeData;
  }

  @Override
  public String get() {
    return fakeData.expression(expressionSupplier.get());
  }

  @JsonCreator
  public static ExpressionSample createFromSample(@JsonProperty("sample") String sampleName,
                                                  @JacksonInject("root") FakeData fakeData) {
    return new ExpressionSample(() -> fakeData.sample(sampleName).get().toString(), fakeData);
  }

  @JsonCreator
  public static ExpressionSample createFromExpression(String expression,
                                                      @JacksonInject("root") FakeData fakeData) {
    return new ExpressionSample(() -> expression, fakeData);
  }

}
