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

import io.backpackcloud.fakeomatic.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusTest extends BaseTest {

  @Test
  public void testInformationStatus() {
    for (int i = 100 ; i < 200 ; i++) {
      assertEquals(EndpointResponse.Status.INFORMATIONAL, EndpointResponse.Status.statusOf(i));
    }
  }

  @Test
  public void testSuccessStatus() {
    for (int i = 200 ; i < 300 ; i++) {
      assertEquals(EndpointResponse.Status.SUCCESS, EndpointResponse.Status.statusOf(i));
    }
  }

  @Test
  public void testRedirectionStatus() {
    for (int i = 300 ; i < 400 ; i++) {
      assertEquals(EndpointResponse.Status.REDIRECTION, EndpointResponse.Status.statusOf(i));
    }
  }

  @Test
  public void testClientErrorStatus() {
    for (int i = 400 ; i < 500 ; i++) {
      assertEquals(EndpointResponse.Status.CLIENT_ERROR, EndpointResponse.Status.statusOf(i));
    }
  }

  @Test
  public void testServerErrorStatus() {
    for (int i = 500 ; i < 600 ; i++) {
      assertEquals(EndpointResponse.Status.SERVER_ERROR, EndpointResponse.Status.statusOf(i));
    }
  }

}
