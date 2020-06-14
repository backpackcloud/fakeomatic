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

import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.backpackcloud.fakeomatic.spi.samples.CompositeSample;
import io.backpackcloud.spectaculous.Spec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompositeSampleTest {

  FakeData fakeData;
  Sample   firstName;
  Sample   lastName;
  Random   random = new Random();

  @BeforeEach
  public void init() {
    fakeData = mock(FakeData.class);
    firstName = mock(Sample.class);
    lastName = mock(Sample.class);

    when(fakeData.sample("first_name")).thenReturn(firstName);
    when(fakeData.sample("last_name")).thenReturn(lastName);
    when(firstName.get(any())).thenReturn("Foo");
    when(lastName.get(any())).thenReturn("Bar");
  }

  @Test
  public void testSample() {
    Spec.describe(CompositeSample.class)

        .because("The order of samples needs to be respected")

        .given(new CompositeSample(fakeData, Arrays.asList("first_name", "last_name"), " "))
        .expect("Foo Bar").from(sample -> sample.get(random))

        .given(new CompositeSample(fakeData, Arrays.asList("last_name", "first_name"), " "))
        .expect("Bar Foo").from(sample -> sample.get(random))
    ;
  }

}
