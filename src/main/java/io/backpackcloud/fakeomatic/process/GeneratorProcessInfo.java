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

import io.backpackcloud.fakeomatic.spi.EndpointResponse;
import io.backpackcloud.fakeomatic.spi.Statistics;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorProcessInfo {

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private AtomicInteger totalResponses = new AtomicInteger(0);

  private EnumMap<EndpointResponse.Status, AtomicInteger> responseMap = new EnumMap<>(EndpointResponse.Status.class);

  public GeneratorProcessInfo() {
    Arrays.stream(EndpointResponse.Status.values())
          .forEach(status -> responseMap.put(status, new AtomicInteger(0)));
  }

  void startNow() {
    this.startTime = LocalDateTime.now();
  }

  void endNow() {
    this.endTime = LocalDateTime.now();
  }

  void update(EndpointResponse response) {
    totalResponses.incrementAndGet();
    responseMap.get(response.status()).incrementAndGet();
  }

  public Statistics toStatistics() {
    return new Statistics(
        responseMap.get(EndpointResponse.Status.INFORMATIONAL).get(),
        responseMap.get(EndpointResponse.Status.SUCCESS).get(),
        responseMap.get(EndpointResponse.Status.REDIRECTION).get(),
        responseMap.get(EndpointResponse.Status.CLIENT_ERROR).get(),
        responseMap.get(EndpointResponse.Status.SERVER_ERROR).get(),
        startTime,
        endTime
    );
  }

}
