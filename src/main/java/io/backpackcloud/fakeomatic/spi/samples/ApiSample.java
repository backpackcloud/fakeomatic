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

package io.backpackcloud.fakeomatic.spi.samples;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * This sample actually calls a given API to get data to use every time it's asked for a data.
 * <p>
 * Due to the nature of this sample, it's not possible to reproduce the same payloads without relying on the
 * dependent API.
 *
 * @author Marcelo Guimarães
 */
@RegisterForReflection
public class ApiSample implements Sample {

  private final URL          url;
  private final String       responsePath;
  private final WebClient    client;
  private final ObjectMapper mapper;

  @JsonCreator
  public ApiSample(@JacksonInject Vertx vertx,
                   @JsonProperty("url") String url,
                   @JsonProperty("result") String responsePath,
                   @JsonProperty("options") Map<String, Object> options) throws MalformedURLException {
    this.mapper = new ObjectMapper();
    this.url = new URL(url);
    this.responsePath = responsePath;
    this.client = WebClient.create(vertx, new WebClientOptions(
        new JsonObject(options == null ? Collections.emptyMap() : options))
        .setDefaultHost(this.url.getHost())
        .setDefaultPort(this.url.getPort() == -1 ? this.url.getDefaultPort() : this.url.getPort())
        .setSsl("https".equals(this.url.getProtocol()))
    );
  }

  @Override
  public String get(Random random) {
    String response = this.client.get(url.getPath())
                                 .send()
                                 .onItem()
                                 .apply(HttpResponse::bodyAsString)
                                 .await().indefinitely();

    try {
      JsonNode parsedPayload = mapper.readTree(response);
      return parsedPayload.at(responsePath).asText();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

}
