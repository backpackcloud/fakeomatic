package io.backpackcloud.fakeomatic.infra;

import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import javax.enterprise.context.ApplicationScoped;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class EndpointConnection implements Endpoint {

  private final URL       url;
  private final WebClient client;

  public EndpointConnection(Config.EndpointConfig config, Vertx vertx) throws MalformedURLException {
    this.url = new URL(config.url());
    this.client = WebClient.create(vertx, new WebClientOptions()
        .setMaxPoolSize(config.concurrency())
        .setDefaultHost(this.url.getHost())
        .setDefaultPort(this.url.getPort() == -1 ? this.url.getDefaultPort() : this.url.getPort())
        .setSsl("https".equals(this.url.getProtocol()))
        .setTrustAll(config.trustAllCertificates())
    );
  }

  @Override
  public CompletionStage<String> postPayload(String contentType, String payload) {
    return CompletableFuture.supplyAsync(() -> client.post(url.getPath())
                                                     .putHeader("Content-Type", contentType)
                                                     .sendBuffer(io.vertx.mutiny.core.buffer.Buffer.buffer(payload))
                                                     .onItem()
                                                     .apply(HttpResponse::bodyAsString)
                                                     .await().indefinitely());
  }

}
