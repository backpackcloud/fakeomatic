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
import io.backpackcloud.fakeomatic.spi.EndpointResponse;
import io.backpackcloud.fakeomatic.spi.EventTrigger;
import io.backpackcloud.fakeomatic.spi.Events;
import io.backpackcloud.fakeomatic.spi.FakeOMatic;
import io.backpackcloud.fakeomatic.spi.ResponseReceivedEvent;
import io.backpackcloud.zipper.UnbelievableException;
import io.quarkus.runtime.QuarkusApplication;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class Generator implements QuarkusApplication, Events {

  private static final Logger LOGGER = Logger.getLogger(Generator.class);

  private final Config       config;
  private final FakeOMatic   fakeOMatic;
  private final EventTrigger eventTrigger;

  public Generator(Config config, FakeOMatic fakeOMatic, EventTrigger eventTrigger) {
    this.config = config;
    this.fakeOMatic = fakeOMatic;
    this.eventTrigger = eventTrigger;
  }

  @Override
  public int run(String... args) {
    int total       = config.total();
    int progressLog = Math.max(total / 100, 1);

    Endpoint endpoint = fakeOMatic.endpoint(config.endpoint())
                                  .orElseThrow(UnbelievableException::new);

    GeneratorProcessInfo info = new GeneratorProcessInfo();

    LOGGER.infof("Starting process... will generate %d payloads", total);
    info.startNow();
    for (int i = 1; i <= total; i++) {
      if (i % progressLog == 0) {
        LOGGER.infof("Sending payload %d of %d", i, total);
      }
      endpoint.call()
              .exceptionally(logError(i))
              .thenAccept(triggerEvents(i).andThen(updateStatistics(info)));
    }

    endpoint.waitForOngoingCalls();
    info.endNow();
    eventTrigger.trigger(FINISHED, info.toStatistics());
    return 0;
  }

  private Function<Throwable, EndpointResponse> logError(int index) {
    return throwable -> {
      LOGGER.errorv(throwable, "Error while sending payload ({0})", index) ;
      return null;
    };
  }

  private Consumer<EndpointResponse> updateStatistics(GeneratorProcessInfo statistics) {
    return response -> {
      if (response != null) {
        statistics.update(response);
      }
    };
  }

  private Consumer<EndpointResponse> triggerEvents(int index) {
    return response -> {
      if (response != null) {
        ResponseReceivedEvent event = new ResponseReceivedEvent(index, response);
        eventTrigger.trigger(RESPONSE_RECEIVED, event);
      }
    };
  }

}
