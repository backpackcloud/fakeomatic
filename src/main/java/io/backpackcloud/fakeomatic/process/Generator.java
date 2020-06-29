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

package io.backpackcloud.fakeomatic.process;

import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.backpackcloud.fakeomatic.spi.EventTrigger;
import io.backpackcloud.fakeomatic.spi.Events;
import io.backpackcloud.fakeomatic.spi.PayloadGeneratedEvent;
import io.backpackcloud.fakeomatic.spi.PayloadGenerator;
import io.backpackcloud.fakeomatic.spi.ResponseReceivedEvent;
import io.quarkus.runtime.QuarkusApplication;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class Generator implements QuarkusApplication, Events {

  private static final Logger LOGGER = Logger.getLogger(Generator.class);

  private final Config config;

  private final PayloadGenerator generator;

  private final Endpoint endpoint;

  private final EventTrigger eventTrigger;

  public Generator(Config config, PayloadGenerator generator, Endpoint endpoint, EventTrigger eventTrigger) {
    this.config = config;
    this.generator = generator;
    this.endpoint = endpoint;
    this.eventTrigger = eventTrigger;
  }

  @Override
  public int run(String... args) {
    int    total       = config.generator().total();
    int    concurrency = config.endpoint().concurrency() + config.generator().buffer();
    int    progressLog = Math.max(total / 100, 1);
    String payload;

    AtomicInteger count      = new AtomicInteger(0);
    AtomicInteger inProgress = new AtomicInteger(0);

    try {
      for (int i = 0; i < total; ) {
        if (inProgress.get() < concurrency) {
          i++;
          inProgress.incrementAndGet();

          if (i % progressLog == 0) {
            LOGGER.infof("Generating payload %d of %d", i, total);
          }

          payload = generator.generate();
          eventTrigger.trigger(PAYLOAD_GENERATED, new PayloadGeneratedEvent(i, payload));
          endpoint.postPayload(generator.contentType(), payload)
                  .exceptionally(logError(i))
                  .thenAccept(logResponse(i))
                  .thenRun(inProgress::decrementAndGet)
                  .thenRun(count::incrementAndGet);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error while injecting payload.", e);
    } finally {
      while (count.get() < total) {
        LOGGER.infof("Waiting for (%d) ongoing requests to finish...", (total - count.get()));
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          LOGGER.error("Error while waiting", e);
        }
      }
    }
    eventTrigger.trigger(FINISHED, total);

    return 0;
  }

  private Function<Throwable, HttpResponse> logError(int index) {
    return throwable -> {
      LOGGER.error(String.format("Error while sending payload (%d)", index), throwable);
      return null;
    };
  }

  private Consumer<HttpResponse> logResponse(int index) {
    return response -> {
      if (response != null) {
        int statusCode = response.statusCode();
        ResponseReceivedEvent event = new ResponseReceivedEvent(
            index,
            statusCode,
            response.statusMessage(),
            response.bodyAsString()
        );
        if (statusCode % 400 < 100) {
          eventTrigger.trigger(CLIENT_ERROR, event);
        } else if (statusCode % 500 < 100) {
          eventTrigger.trigger(SERVER_ERROR, event);
        } else if (statusCode % 200 < 100) {
          eventTrigger.trigger(RESPONSE_OK, event);
        }
      }
    };
  }

}
