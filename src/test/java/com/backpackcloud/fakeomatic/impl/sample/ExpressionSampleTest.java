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

package com.backpackcloud.fakeomatic.impl.sample;

import com.backpackcloud.fakeomatic.impl.BaseTest;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import org.junit.jupiter.api.Test;

public class ExpressionSampleTest extends BaseTest {

  @Test
  public void testSampleExpression() {
    Sampler sampler = createSampler("expressions.yaml");
    Sample<String> sample = sampler.<String>sample("address").get();
    times(100000, sample, address -> {
      address.matches("^(Some Street|Another Street|Galaxy) (\\d{2,3})$");
    });
  }

  @Test
  public void testStringExpression() {
    Sampler sampler = createSampler("expressions.yaml");
    Sample<String> sample = sampler.<String>sample("credit_card").get();
    times(100000, sample, address -> {
      address.matches("^\\d{16}$");
    });
  }

}
