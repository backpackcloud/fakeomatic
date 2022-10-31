package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RegisterForReflection
public class HttpSample implements Sample<String> {

  public static final String TYPE = "http";

  private final HttpClient client;
  private final HttpRequest request;

  public HttpSample(@JsonProperty("url") Configuration location,
                    @JsonProperty("timeout") int timeout,
                    @JsonProperty("headers") Map<String, Configuration> headers) {
    client = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .connectTimeout(Duration.of(timeout > 0 ? timeout : 15, ChronoUnit.SECONDS))
      .followRedirects(HttpClient.Redirect.ALWAYS)
      .build();
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
      .GET()
      .uri(URI.create(location.get()));

    if (headers != null) {
      headers.forEach((name, value) -> requestBuilder.header(name, value.get()));
    }

    request = requestBuilder
      .build();
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String get() {
    try {
      return client.send(request, HttpResponse.BodyHandlers.ofString()).body().strip();
    } catch (Exception e) {
      throw new UnbelievableException(e);
    }
  }

}
