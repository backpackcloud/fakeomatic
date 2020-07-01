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
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.backpackcloud.fakeomatic.spi.EndpointResponse;
import io.backpackcloud.fakeomatic.spi.PayloadTemplate;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

@RegisterForReflection
public class VertxEndpoint implements Endpoint {

  private static final Logger LOGGER = Logger.getLogger(VertxEndpoint.class);

  private final URL                 url;
  private final HttpRequest<Buffer> request;

  private final int concurrency;

  private AtomicInteger inProgress = new AtomicInteger(0);


  public VertxEndpoint(URL url, HttpRequest<Buffer> request, int concurrency) {
    this.url = url;
    this.request = request;
    this.concurrency = concurrency;
  }

  @Override
  public URL url() {
    return url;
  }

  @Override
  public Optional<PayloadTemplate> template() {
    return Optional.empty();
  }

  @Override
  public void waitForOngoingCalls() {
    int wait = 100;
    while (inProgress.get() > 0) {
      LOGGER.infof("Waiting for (%d) ongoing requests to finish...", inProgress.get());
      try {
        Thread.sleep(wait *= 1.5);
      } catch (InterruptedException e) {
        LOGGER.error(e);
      }
    }
  }

  @Override
  public CompletionStage<EndpointResponse> call() {
    int wait = 10;
    while (inProgress.get() >= concurrency) {
      try {
        Thread.sleep(wait *= 1.2);
      } catch (InterruptedException e) {
        LOGGER.error(e);
      }
    }
    return CompletableFuture.supplyAsync(
        () -> createResponse(request.send())
    );
  }

  private Response createResponse(Uni<HttpResponse<Buffer>> response) {
    return response.onItem()
                   .apply(httpResponse -> {
                     try {
                       return new Response(httpResponse);
                     } finally {
                       inProgress.decrementAndGet();
                     }
                   })
                   .await().atMost(Duration.ofSeconds(30));
  }

  @JsonCreator
  public static Endpoint create(@JacksonInject Vertx vertx,
                                @JsonProperty("url") Configuration location,
                                @JsonProperty("method") String method,
                                @JsonProperty("headers") Map<String, Configuration> endpointHeaders,
                                @JsonProperty("params") Map<String, Configuration> params,
                                @JsonProperty("concurrency") int concurrency,
                                @JsonProperty("buffer") int buffer,
                                @JsonProperty("insecure") boolean insecure) {
    try {
      String requestURI = location.get();
      for (Map.Entry<String, Configuration> entry : params.entrySet()) {
        requestURI = requestURI.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().get());
      }
      URL url = new URL(requestURI);
      WebClient client = WebClient.create(vertx, new WebClientOptions()
          .setMaxPoolSize(concurrency)
          .setDefaultHost(url.getHost())
          .setDefaultPort(url.getPort() == -1 ? url.getDefaultPort() : url.getPort())
          .setSsl("https".equals(url.getProtocol()))
          .setTrustAll(insecure)
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
      return new VertxEndpoint(url, request, concurrency + buffer);
    } catch (Exception e) {
      throw new UnbelievableException(e);
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
