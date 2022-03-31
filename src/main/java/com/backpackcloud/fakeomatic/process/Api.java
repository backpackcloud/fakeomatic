package com.backpackcloud.fakeomatic.process;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Api implements QuarkusApplication {

  private final ApiVerticle verticle;
  private final Vertx vertx;

  public Api(ApiVerticle verticle, Vertx vertx) {
    this.verticle = verticle;
    this.vertx = vertx;
  }

  @Override
  public int run(String... args) {
    vertx.deployVerticle(verticle).await().indefinitely();
    Quarkus.waitForExit();
    return 0;
  }

}
