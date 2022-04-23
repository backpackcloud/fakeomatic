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
import com.backpackcloud.fakeomatic.sampler.impl.sample.JoiningSample;
import com.backpackcloud.fakeomatic.sampler.impl.sample.ListSample;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoiningSampleTest extends BaseTest {

  Sampler sampler = createSampler("join.yaml");

  @Test
  public void testParse() {
    Map<String, Sample> samples = sampler.samples();
    assertEquals(6, samples.size());
    testParse("first_name", "Atadolfo", "Biru", "Calicusco", "Danete", "Elisvaldino");
    testParse("middle_name", "Lombardino", "Jurubebo", "Molibidemo");
    testParse("last_name", "Silva", "Sousa", "Souza");
  }

  private void testParse(String name, String... values) {
    ListSample<String> sample = (ListSample) sampler.sample(name).get();
    List<String> sampleValues = sample.samples()
      .stream()
      .map(Sample::get)
      .collect(Collectors.toList());
    assertNotNull(sample);
    assertEquals(values.length, sampleValues.size());
    for (String value : values) {
      assertTrue(sampleValues.contains(value));
    }
  }

  @Test
  public void test1() {
    test("test1");
  }

  @Test
  public void test2() {
    test("test2");
  }

  @Test
  public void test3() {
    test("test3");
  }

  private void test(String sampleName) {
    JoiningSample sample = (JoiningSample) sampler.<String>sample(sampleName).get();
    List<Sample> samples = sample.samples();
    times(1000, () -> {
      String generated = sample.get();
      String[] strings = generated.split("\\s");
      assertEquals(samples.size(), strings.length);
    });
  }

}
