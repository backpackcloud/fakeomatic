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

package com.backpackcloud.fakeomatic.impl;

import com.backpackcloud.fakeomatic.spi.Endpoint;
import com.backpackcloud.zipper.Configuration;
import com.backpackcloud.zipper.UnbelievableException;
import io.quarkus.runtime.annotations.RegisterForReflection;
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

@RegisterForReflection
public class VertxEndpoint implements Endpoint {

  private static final Logger LOGGER = Logger.getLogger(VertxEndpoint.class);

  private final URL url;
  private final HttpRequest<Buffer> request;

  public VertxEndpoint(URL url, HttpRequest<Buffer> request) {
    this.url = url;
    this.request = request;
  }

  @Override
  public URL url() {
    return url;
  }

  @Override
  public CompletionStage<String> call() {
    return CompletableFuture.supplyAsync(
      () -> this.request.send()
        .onItem()
        .transform(HttpResponse::bodyAsString)
        .onFailure()
        .invoke(throwable -> LOGGER.error("Error while calling endpoint", throwable))
        .await().atMost(Duration.ofSeconds(30))
    );
  }

  public static Endpoint create(Vertx vertx, Configuration location,
                                Map<String, Configuration> endpointHeaders,
                                Map<String, Configuration> params,
                                Configuration insecure) {
    try {
      String requestURI = location.get();
      if (params != null) {
        for (Map.Entry<String, Configuration> entry : params.entrySet()) {
          requestURI = requestURI.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().get());
        }
      }
      URL url = new URL(requestURI);

      WebClient client = WebClient.create(vertx, new WebClientOptions()
        .setDefaultHost(url.getHost())
        .setDefaultPort(url.getPort() == -1 ? url.getDefaultPort() : url.getPort())
        .setSsl("https".equals(url.getProtocol()))
        .setTrustAll(insecure.orElse(false))
      );
      HttpRequest<Buffer> request = client.get(url.toString());
      if (endpointHeaders != null) {
        for (Map.Entry<String, Configuration> entry : endpointHeaders.entrySet()) {
          request.putHeader(entry.getKey(), entry.getValue().get());
        }
      }
      return new VertxEndpoint(url, request);
    } catch (Exception e) {
      throw new UnbelievableException(e);
    }
  }

}
