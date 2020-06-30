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
import io.backpackcloud.fakeomatic.spi.Faker;
import io.backpackcloud.fakeomatic.spi.samples.WeightedSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeightedSampleTest extends BaseTest {

  Faker faker;

  @BeforeEach
  public void init() {
    faker = createFakeData("weight.yaml");
  }

  @Test
  public void testSample1() {
    testSample("test1");
  }

  @Test
  public void testSample2() {
    testSample("test2");
  }

  @Test
  public void testSample3() {
    testSample("test3");
  }

  @Test
  public void testSample4() {
    testSample("test4");
  }

  @Test
  public void testSample5() {
    testSample("test5");
  }

  @Test
  public void testSample6() {
    testSample("test6");
  }

  private void testSample(String sampleName) {
    WeightedSample                               sample      = (WeightedSample) faker.sample(sampleName);
    List<WeightedSample.WeightedValueDefinition> definitions = sample.definitions();
    Map<Object, Integer>                         occurrences = new HashMap<>();
    Random                                       random      = new Random();
    int                                          total       = 1000000;
    int                                          errorMargin = 1;

    definitions.stream().forEach(def -> occurrences.put(def.sample().get(), 0));

    times(total, () -> occurrences.compute(sample.get(), (o, integer) -> integer + 1));

    assertEquals(total, (Integer) occurrences.values().stream().mapToInt(Integer::intValue).sum());

    assertTrue(
        definitions.stream()
                   .map(def -> def.weight() - occurrences.get(def.sample().get()) * sample.totalWeight() / total)
                   .allMatch(distance -> distance <= errorMargin)
    );
  }

}
