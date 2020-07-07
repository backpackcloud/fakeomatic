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

import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Configuration;
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.backpackcloud.fakeomatic.spi.EndpointResponse;
import io.backpackcloud.fakeomatic.spi.Payload;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@RegisterForReflection
public class VertxEndpoint implements Endpoint {

  private static final Logger LOGGER = Logger.getLogger(VertxEndpoint.class);

  private final URL                 url;
  private final HttpRequest<Buffer> request;
  private final Payload             payload;

  private final int concurrency;

  private final Function<HttpRequest<Buffer>, Uni<HttpResponse<Buffer>>> requestFunction;

  private final AtomicInteger inProgress = new AtomicInteger(0);

  public VertxEndpoint(URL url, Payload payload, HttpRequest<Buffer> request, int concurrency) {
    this.url = url;
    this.request = request;
    this.concurrency = concurrency;
    this.payload = payload;
    if (payload == null) {
      this.requestFunction = HttpRequest::send;
    } else {
      this.requestFunction = httpRequest -> httpRequest.sendBuffer(Buffer.buffer(this.payload.content()));
    }
  }

  @Override
  public URL url() {
    return url;
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
    inProgress.incrementAndGet();
    return CompletableFuture.supplyAsync(
        () -> requestFunction.apply(this.request)
                             .onItem()
                             .apply(httpResponse -> {
                               inProgress.decrementAndGet();
                               return new Response(httpResponse);
                             })
                             .onFailure()
                             .invoke(throwable -> {
                               inProgress.decrementAndGet();
                               LOGGER.error("Error while calling endpoint", throwable);
                             })
                             .await().atMost(Duration.ofSeconds(30))
    );
  }

  public static Endpoint create(Vertx vertx, Configuration location,
                                Configuration method,
                                Payload payload,
                                Map<String, Configuration> endpointHeaders,
                                Map<String, Configuration> params,
                                Configuration concurrency,
                                Configuration buffer,
                                Configuration insecure) {
    try {
      String requestURI = location.get();
      if (params != null) {
        for (Map.Entry<String, Configuration> entry : params.entrySet()) {
          requestURI = requestURI.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().get());
        }
      }
      URL url         = new URL(requestURI);
      int maxPoolSize = concurrency.or(10);

      WebClient client = WebClient.create(vertx, new WebClientOptions()
          .setMaxPoolSize(maxPoolSize)
          .setDefaultHost(url.getHost())
          .setDefaultPort(url.getPort() == -1 ? url.getDefaultPort() : url.getPort())
          .setSsl("https".equals(url.getProtocol()))
          .setTrustAll(insecure.or(false))
      );
      HttpRequest<Buffer> request = client.request(
          HttpMethod.valueOf(method.or(payload == null ? "GET" : "POST").toUpperCase()),
          url.toString()
      );
      if (endpointHeaders != null) {
        for (Map.Entry<String, Configuration> entry : endpointHeaders.entrySet()) {
          request.putHeader(entry.getKey(), entry.getValue().get());
        }
        if (payload != null) {
          request.putHeader("Content-Type", payload.contentType());
        }
      }
      return new VertxEndpoint(
          url,
          payload,
          request,
          maxPoolSize + buffer.or(10)
      );
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

    @Override
    public String toString() {
      return body();
    }
  }

}
