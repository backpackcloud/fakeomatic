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
import io.backpackcloud.fakeomatic.spi.samples.CompositeSample;
import io.backpackcloud.fakeomatic.spi.samples.ListSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompositeSampleTest extends BaseTest {

  FakeData fakeData;

  @BeforeEach
  public void init() {
    fakeData = createFakeData("composite.yaml");
  }

  @Test
  public void testParse() {
    List<Sample> samples = fakeData.samples();
    assertEquals(6, samples.size());
    testParse("first_name", "Atadolfo", "Biru", "Calicusco", "Danete", "Elisvaldino");
    testParse("middle_name", "Lombardino", "Jurubebo", "Molibidemo");
    testParse("last_name", "Silva", "Sousa", "Souza");
  }

  private void testParse(String name, String... values) {
    ListSample sample       = (ListSample) fakeData.sample(name);
    List       sampleValues = sample.values();
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
    CompositeSample sample  = (CompositeSample) fakeData.sample(sampleName);
    List<Sample>    samples = sample.samples();
    times(1000, () -> {
      String   generated = sample.get(random);
      String[] strings   = generated.split("\\s");
      assertEquals(samples.size(), strings.length);
      for (int i = 0; i < strings.length; i++) {
        ListSample listSample = (ListSample) samples.get(i);
        assertTrue(listSample.values().contains(strings[i]));
      }
    });
  }

}
