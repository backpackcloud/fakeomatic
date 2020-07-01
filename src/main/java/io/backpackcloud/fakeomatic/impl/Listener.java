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

import io.backpackcloud.fakeomatic.spi.Events;
import io.backpackcloud.fakeomatic.spi.PayloadGeneratedEvent;
import io.backpackcloud.fakeomatic.spi.ResponseReceivedEvent;
import io.quarkus.vertx.ConsumeEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Listener implements Events {

  private static final Logger LOGGER = Logger.getLogger(Listener.class);

  private final AtomicInteger serverErrors = new AtomicInteger(0);
  private final AtomicInteger clientErrors = new AtomicInteger(0);
  private final AtomicInteger ok           = new AtomicInteger(0);

  @ConsumeEvent(PAYLOAD_GENERATED)
  public void onPayloadGenerated(PayloadGeneratedEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debugf("Payload %d generated: %s", event.index(), event.payload());
    }
  }

  @ConsumeEvent(CLIENT_ERROR)
  public void onClientError(ResponseReceivedEvent event) {
    LOGGER.warnf(
        "Got a client error response for payload %d (%d): %s",
        event.index(), event.response().statusCode(), event.response().statusMessage()
    );
    this.clientErrors.incrementAndGet();
  }

  @ConsumeEvent(SERVER_ERROR)
  public void onServerError(ResponseReceivedEvent event) {
    LOGGER.errorf(
        "Got a server error response for payload %d (%d): %s",
        event.index(), event.response().statusCode(), event.response().statusMessage()
    );
    this.serverErrors.incrementAndGet();
  }

  @ConsumeEvent(RESPONSE_OK)
  public void onOk(ResponseReceivedEvent event) {
    this.ok.incrementAndGet();
    LOGGER.debugf("Received response for payload %d: %s", event.index(), event.response().body());
  }

  @ConsumeEvent(value = FINISHED)
  public void onFinish(int total) {
    LOGGER.infof(
        "Finished generating %d payloads. OKs (%d) | Server Errors (%d) | Client Errors (%d)",
        total, ok.get(), serverErrors.get(), clientErrors.get()
    );
  }

}
