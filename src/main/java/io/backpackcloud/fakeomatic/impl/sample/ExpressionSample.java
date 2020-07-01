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

package io.backpackcloud.fakeomatic.impl.sample;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Faker;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;

import java.util.function.Supplier;

@RegisterForReflection
public class ExpressionSample implements Sample<String> {

  private static final Logger LOGGER = Logger.getLogger(ExpressionSample.class);

  private final Faker            faker;
  private final Supplier<String> expressionSupplier;

  public ExpressionSample(Supplier<String> expressionSupplier, Faker faker) {
    this.expressionSupplier = expressionSupplier;
    this.faker = faker;
  }

  @Override
  public String get() {
    String expression = expressionSupplier.get();
    String result = faker.expression(expression);
    LOGGER.debugv("Creating from expression {0}: {1}", expression, result);
    return result;
  }

  // TODO use sample configuration
  @JsonCreator
  public static ExpressionSample create(@JsonProperty("sample") String sampleName,
                                        @JsonProperty("expression") String expression,
                                        @JacksonInject("root") Faker faker) {
    if (sampleName != null) {
      return new ExpressionSample(faker.sample(sampleName), faker);
    } else if (expression != null) {
      return new ExpressionSample(() -> expression, faker);
    } else {
      throw new UnbelievableException("No sample or expression given.");
    }
  }

}
