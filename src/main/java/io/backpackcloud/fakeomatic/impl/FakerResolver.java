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

import io.backpackcloud.fakeomatic.core.spi.Faker;
import io.backpackcloud.zipper.UnbelievableException;
import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Expression;
import io.quarkus.qute.Results;
import io.quarkus.qute.ValueResolver;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class FakerResolver implements ValueResolver {

  private static final Logger LOGGER = Logger.getLogger(FakerResolver.class);

  @Override
  public CompletionStage<Object> resolve(EvalContext context) {
    try {
      Faker faker = (Faker) context.getBase();
      List params = context.getParams()
                           .stream()
                           .map(Expression::getLiteralValue)
                           .map(future -> {
                             try {
                               return future.get();
                             } catch (Exception e) {
                               LOGGER.error("Error while evaluating params", e);
                               throw new UnbelievableException(e);
                             }
                           }).collect(Collectors.toList());
      switch (context.getName()) {
        case "some":
          return CompletableFuture.completedFuture(faker.some(params.get(0).toString()));
        case "expression":
          return CompletableFuture.completedFuture(faker.expression(params.get(0).toString()));
        default:
          return Results.NOT_FOUND;
      }
    } catch (Exception e) {
      LOGGER.error("Error while resolving sample", e);
      throw new UnbelievableException(e);
    }

  }

  @Override
  public boolean appliesTo(EvalContext context) {
    return ValueResolver.matchClass(context, Faker.class);
  }

}
