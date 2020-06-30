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
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Configuration;
import io.backpackcloud.fakeomatic.spi.EndpointClient;
import io.backpackcloud.fakeomatic.spi.EndpointResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RegisterForReflection
public class VertxEndpointClient implements EndpointClient {

  private final HttpRequest<Buffer> request;

  public VertxEndpointClient(HttpRequest<Buffer> request) {
    this.request = request;
  }

  @Override
  public CompletionStage<EndpointResponse> send() {
    return CompletableFuture.supplyAsync(
        () -> createResponse(request.send())
    );
  }

  @Override
  public CompletionStage<EndpointResponse> send(String payload) {
    return CompletableFuture.supplyAsync(
        () -> createResponse(request.sendBuffer(Buffer.buffer(payload)))
    );
  }

  private Response createResponse(Uni<HttpResponse<Buffer>> response) {
    return response.onItem()
                   .apply(Response::new)
                   .await().atMost(Duration.ofSeconds(10));
  }

  @JsonCreator
  public static EndpointClient create(@JacksonInject Vertx vertx,
                                      @JsonProperty("url") Configuration location,
                                      @JsonProperty("method") String method,
                                      @JsonProperty("headers") Map<String, Configuration> endpointHeaders,
                                      @JsonProperty("path_vars") Map<String, Configuration> pathVars,
                                      @JsonProperty("options") Options options) {
    try {
      String requestURI = location.get();
      for (Map.Entry<String, Configuration> entry : pathVars.entrySet()) {
        requestURI = requestURI.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().get());
      }
      URL url = new URL(requestURI);
      WebClient client = WebClient.create(vertx, new WebClientOptions()
          .setMaxPoolSize(options.concurrency)
          .setDefaultHost(url.getHost())
          .setDefaultPort(url.getPort() == -1 ? url.getDefaultPort() : url.getPort())
          .setSsl("https".equals(url.getProtocol()))
          .setTrustAll(options.insecure)
      );
      HttpRequest<Buffer> request = client.request(
          HttpMethod.valueOf(Optional.ofNullable(method).map(String::toUpperCase).orElse("GET")),
          url.toString()
      );
      if (endpointHeaders != null) {
        for (Map.Entry<String, Configuration> entry : endpointHeaders.entrySet()) {
          request.putHeader(entry.getKey(), entry.getValue().get());
        }
      }
      return new VertxEndpointClient(request);
    } catch (Exception e) {
      throw new UnbelievableException(e);
    }
  }

  @RegisterForReflection
  public static class Options {

    public final int     concurrency;
    public final boolean insecure;

    @JsonCreator
    public Options(@JsonProperty("concurrency") int concurrency,
                   @JsonProperty("insecure") boolean insecure) {
      this.concurrency = concurrency;
      this.insecure = insecure;
    }

  }

  static class Response implements EndpointResponse {

    private final HttpResponse<Buffer> response;

    Response(HttpResponse<Buffer> response) {
      this.response = response;
    }

    @Override
    public int statusCode() {
      return response.statusCode();
    }

    @Override
    public String statusMessage() {
      return response.statusMessage();
    }

    @Override
    public String body() {
      return response.bodyAsString();
    }

  }

}
