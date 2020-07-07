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

package io.backpackcloud.fakeomatic.spi;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Statistics {

  private final int totalResponses;
  private final int informationalResponses;
  private final int successResponses;
  private final int redirectionResponses;
  private final int clientErrorResponses;
  private final int serverErrorResponses;

  private final long processingTime;

  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  public Statistics(int informationalResponses,
                    int successResponses,
                    int redirectionResponses,
                    int clientErrorResponses,
                    int serverErrorResponses,
                    LocalDateTime startTime,
                    LocalDateTime endTime) {
    this.informationalResponses = informationalResponses;
    this.successResponses = successResponses;
    this.redirectionResponses = redirectionResponses;
    this.clientErrorResponses = clientErrorResponses;
    this.serverErrorResponses = serverErrorResponses;
    this.startTime = startTime;
    this.endTime = endTime;
    this.totalResponses =
        informationalResponses + successResponses + redirectionResponses + clientErrorResponses + serverErrorResponses;
    this.processingTime =
        endTime.toInstant(ZoneOffset.UTC).toEpochMilli() - startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
  }

  public int totalResponses() {
    return this.totalResponses;
  }

  public int informationalResponses() {
    return this.informationalResponses;
  }

  public int successResponses() {
    return this.successResponses;
  }

  public int redirectionResponses() {
    return this.redirectionResponses;
  }

  public int clientErrorResponses() {
    return this.clientErrorResponses;
  }

  public int serverErrorResponses() {
    return this.serverErrorResponses;
  }

  public long processingTime() {
    return this.processingTime;
  }

  public LocalDateTime startTime() {
    return this.startTime;
  }

  public LocalDateTime endTime() {
    return this.endTime;
  }

}
