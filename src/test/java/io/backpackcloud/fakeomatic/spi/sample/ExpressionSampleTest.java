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

package io.backpackcloud.fakeomatic.spi.sample;

import io.backpackcloud.fakeomatic.BaseTest;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import org.junit.jupiter.api.Test;

public class ExpressionSampleTest extends BaseTest {

  @Test
  public void testSampleExpression() {
    FakeData fakeData = createFakeData("expressions.yaml");
    Sample<String> sample = fakeData.sample("address");
    times(100000, sample, address -> {
      address.matches("^(Some Street|Another Street|Galaxy) (\\d{2,3})$");
    });
  }

  @Test
  public void testStringExpression() {
    FakeData fakeData = createFakeData("expressions.yaml");
    Sample<String> sample = fakeData.sample("credit_card");
    times(100000, sample, address -> {
      address.matches("^\\d{16}$");
    });
  }

}
