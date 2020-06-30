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

import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import javax.enterprise.context.ApplicationScoped;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@ApplicationScoped
public class EndpointConnection implements Endpoint {

  private final URL                 url;
  private final WebClient           client;
  private final Map<String, String> headers;

  public EndpointConnection(Config.EndpointConfig config, Vertx vertx) throws MalformedURLException {
    this.url = new URL(config.url());
    this.client = WebClient.create(vertx, new WebClientOptions()
        .setMaxPoolSize(config.concurrency())
        .setDefaultHost(this.url.getHost())
        .setDefaultPort(this.url.getPort() == -1 ? this.url.getDefaultPort() : this.url.getPort())
        .setSsl("https".equals(this.url.getProtocol()))
        .setTrustAll(config.insecure())
    );
    this.headers = config.headers();
  }

  @Override
  public CompletionStage<HttpResponse> postPayload(String contentType, String payload) {
    return CompletableFuture.supplyAsync(() -> {
      HttpRequest<Buffer> request = client.post(url.toString());
      for (Map.Entry<String, String> header : this.headers.entrySet()) {
        request.putHeader(header.getKey(), header.getValue());
      }
      return request
          .putHeader("Content-Type", contentType)
          .sendBuffer(Buffer.buffer(payload))
          .onItem()
          .apply(Function.identity())
          // TODO externalize this
          .await().atMost(Duration.ofSeconds(30));
    });
  }

}
