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
import io.backpackcloud.fakeomatic.spi.PayloadGenerator;
import io.quarkus.runtime.QuarkusApplication;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Generator implements QuarkusApplication {

  private static final Logger LOGGER = Logger.getLogger(Generator.class);

  private final Config config;

  private final PayloadGenerator generator;

  private final Endpoint endpoint;

  public Generator(Config config, PayloadGenerator generator, @RestClient Endpoint endpoint) {
    this.config = config;
    this.generator = generator;
    this.endpoint = endpoint;
  }

  @Override
  public int run(String... args) throws Exception {
    int progressLog = Math.max(config.total() / 100, 1);

    AtomicInteger count      = new AtomicInteger(0);
    AtomicInteger inProgress = new AtomicInteger(0);
    // TODO add a buffer
    for (int i = 0; i < config.total(); ) {
      if (inProgress.get() < config.concurrency()) {
        i++;
        inProgress.incrementAndGet();

        if (i % progressLog == 0) {
          LOGGER.infof("Generating payload %d of %d", i, config.total());
        }

        endpoint.inject(generator.contentType(), generator.generate())
                .exceptionally(Throwable::getMessage)
                .thenRun(inProgress::decrementAndGet)
                .thenRun(count::incrementAndGet);
      }
    }

    while (count.get() < config.total()) {
      LOGGER.infof("Waiting for (%d) ongoing requests to finish...", (config.total() - count.get()));
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        LOGGER.error(e);
      }
    }
    return 0;
  }

}
