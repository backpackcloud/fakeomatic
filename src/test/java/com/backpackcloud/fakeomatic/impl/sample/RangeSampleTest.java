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
import com.backpackcloud.fakeomatic.sampler.impl.sample.RangeSample;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeSampleTest extends BaseTest {

  Sampler sampler = createSampler("ranges.yaml");

  @Test
  public void testParse() {
    Map<String, Sample> samples = sampler.samples();
    assertEquals(2, samples.size());
  }

  @Test
  public void test1() {
    testSample("test1", -100, 100);
  }

  @Test
  public void test2() {
    testSample("test2", 20, 40);
  }

  private void testSample(String sampleName, int min, int max) {
    Sample sample = sampler.sample(sampleName).get();
    RangeSample rangeSample = (RangeSample) sample;
    Set<Integer> generated = new HashSet<>();

    assertEquals(min, rangeSample.min());
    assertEquals(max, rangeSample.max());

    times(100000, rangeSample, i -> {
      generated.add(i);
      assertTrue(i >= min);
      assertTrue(i <= max);
    });

    assertEquals(max - min + 1, generated.size());
  }

}
