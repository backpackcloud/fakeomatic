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

package com.backpackcloud.fakeomatic.impl.sample;

import com.backpackcloud.fakeomatic.spi.Endpoint;
import com.backpackcloud.fakeomatic.spi.EndpointResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import com.backpackcloud.zipper.UnbelievableException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;

import java.util.concurrent.ExecutionException;

/**
 * This sample actually calls a given API to get data to use every time it's asked for a data.
 * <p>
 * Due to the nature of this sample, it's not possible to reproduce the same payloads without relying on the
 * dependent API.
 *
 * @author Marcelo Guimarães
 */
@RegisterForReflection
public class ApiSample implements Sample<EndpointResponse> {

  private static final Logger LOGGER = Logger.getLogger(ApiSample.class);

  private final Endpoint endpoint;

  @JsonCreator
  public ApiSample(@JsonProperty("endpoint") Endpoint endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public EndpointResponse get() {
    try {
      return endpoint.call().toCompletableFuture().get();
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error(e);
    }
    throw new UnbelievableException("Unable to call the endpoint");
  }

}
