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

package io.backpackcloud.fakeomatic.impl.resolver;

import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Results;
import io.quarkus.qute.ValueResolver;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FakeDataResolver implements ValueResolver {

  private static final Logger LOGGER = Logger.getLogger(FakeDataResolver.class);

  @Override
  public CompletionStage<Object> resolve(EvalContext context) {
    try {
      FakeData fakeData = (FakeData) context.getBase();
      String   param    = context.getParams().get(0).getLiteralValue().get().toString();
      switch (context.getName()) {
        case "fake":
          return CompletableFuture.completedFuture(fakeData.fake(param).toString());
        case "expression":
          return CompletableFuture.completedFuture(fakeData.expression(param));
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
    return ValueResolver.matchClass(context, FakeData.class);
  }

}
