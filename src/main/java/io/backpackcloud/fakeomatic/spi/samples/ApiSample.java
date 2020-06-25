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
import io.backpackcloud.fakeomatic.spi.Configuration;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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

  private static final Logger LOGGER = Logger.getLogger(ApiSample.class);

  private final URL              url;
  private final String           method;
  private final String           returnPath;
  private final WebClient        client;
  private final ObjectMapper     mapper;
  private final TemplateInstance templateInstance;
  private final Payload          payload;

  @JsonCreator
  public ApiSample(@JacksonInject("root") FakeData fakeData,
                   @JacksonInject Vertx vertx,
                   @JsonProperty("url") Configuration url,
                   @JsonProperty("method") String method,
                   @JsonProperty("payload") Payload payload,
                   @JsonProperty("return") String returnPath,
                   @JsonProperty("insecure") boolean insecure,
                   @JsonProperty("options") Map<String, Object> options) {
    try {
      this.payload = payload;
      if (payload != null) {
        this.templateInstance = Engine.builder()
                                      .addDefaults()
                                      .build()
                                      .parse(payload.template())
                                      .data(fakeData);
      } else {
        this.templateInstance = null;
      }
      this.method = Optional.ofNullable(method).orElse("get");
      this.mapper = new ObjectMapper();
      this.url = new URL(url.get());
      this.returnPath = Optional.ofNullable(returnPath).orElse("/");
      this.client = WebClient.create(vertx, new WebClientOptions(
          new JsonObject(options == null ? Collections.emptyMap() : options))
          .setDefaultHost(this.url.getHost())
          .setDefaultPort(this.url.getPort() == -1 ? this.url.getDefaultPort() : this.url.getPort())
          .setSsl("https".equals(this.url.getProtocol()))
          .setTrustAll(insecure)
      );
    } catch (Exception e) {
      LOGGER.error("Error while creating ApiSample", e);
      throw new UnbelievableException(e);
    }
  }

  @Override
  public Object get() {
    HttpRequest<Buffer>       request = this.client.raw(this.method.toUpperCase(), url.toString());
    Uni<HttpResponse<Buffer>> response;
    if (this.templateInstance != null) {
      response = request
          .putHeader("Content-Type", payload.type())
          .sendBuffer(Buffer.buffer(templateInstance.render()));
    } else {
      response = request.send();
    }
    String responseBody = response.onItem()
                                  .apply(HttpResponse::bodyAsString)
                                  // TODO add a fallback sample
                                  // TODO externalize timeout
                                  .await().atMost(Duration.ofSeconds(30));
    try {
      JsonNode parsedPayload = this.mapper.readTree(responseBody);
      return parsedPayload.at(this.returnPath);
    } catch (IOException e) {
      LOGGER.error("Error while calling API", e);
      throw new UnbelievableException(e);
    }
  }

}
