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

package com.backpackcloud.fakeomatic.spi;

import com.backpackcloud.fakeomatic.impl.VertxEndpoint;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.backpackcloud.zipper.Configuration;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.mutiny.core.Vertx;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@RegisterForReflection
public interface Endpoint {

  URL url();

  CompletionStage<EndpointResponse> call();

  void waitForOngoingCalls();

  @JsonCreator
  static Endpoint newInstance(@JacksonInject Vertx vertx,
                              @JsonProperty("url") Configuration location,
                              @JsonProperty("payload") Payload payload,
                              @JsonProperty("method") Configuration method,
                              @JsonProperty("headers") Map<String, Configuration> endpointHeaders,
                              @JsonProperty("params") Map<String, Configuration> params,
                              @JsonProperty("concurrency") Configuration concurrency,
                              @JsonProperty("buffer") Configuration buffer,
                              @JsonProperty("insecure") Configuration insecure) {
    return VertxEndpoint.create(vertx, location, method, payload, endpointHeaders, params,
        concurrency, buffer, insecure);
  }

}
