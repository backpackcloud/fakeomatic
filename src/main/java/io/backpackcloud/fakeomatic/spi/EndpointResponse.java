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

import io.backpackcloud.fakeomatic.UnbelievableException;

public interface EndpointResponse {

  int statusCode();

  String statusMessage();

  String body();

  default Status status() {
    return Status.statusOf(statusCode());
  }

  enum Status {

    INFORMATIONAL(100),
    SUCCESS(200),
    REDIRECTION(300),
    CLIENT_ERROR(400),
    SERVER_ERROR(500);

    private final int baseStatus;

    Status(int baseStatus) {
      this.baseStatus = baseStatus;
    }

    static Status statusOf(int statusCode) {
      for(Status status : values()) {
        if (statusCode % status.baseStatus < 100) {
          return status;
        }
      }
      throw new UnbelievableException("Invalid status code: " + statusCode);
    }

  }

}