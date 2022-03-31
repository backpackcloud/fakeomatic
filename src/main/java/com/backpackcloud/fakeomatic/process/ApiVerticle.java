package com.backpackcloud.fakeomatic.process;

import com.backpackcloud.fakeomatic.core.spi.Faker;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ApiVerticle extends AbstractVerticle {

  @ConfigProperty(name = "quarkus.http.host", defaultValue = "0.0.0.0")
  String host;

  @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
  int port;

  private final Faker faker;

  public ApiVerticle(Faker faker) {
    this.faker = faker;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    router.route(HttpMethod.GET, "/fake/:sample").handler(routingContext -> {
      String sample = routingContext.request().getParam("sample");

      Optional<Object> data = faker.sample(sample).map(Sample::get);

      if (data.isPresent()) {
        String value = data.get().toString();
        routingContext.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/text; charset=utf-8")
          .end(value, "utf-8");
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    });

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, host)
      .onComplete(event -> startPromise.complete());
  }

}