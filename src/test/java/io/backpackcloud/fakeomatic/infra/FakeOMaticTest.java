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

package io.backpackcloud.fakeomatic.infra;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FakeOMaticTest {

  @Test
  public void testNumberGeneration() {
    FakeOMatic   fake    = new FakeOMatic(new Random());
    Set<Integer> numbers = new HashSet<>();

    for (int i = 0; i < 10000; i++) {
      int number = fake.number(1, 50);
      assertTrue(number >= 1);
      assertTrue(number <= 50);
      numbers.add(number);
    }

    assertEquals(50, numbers.size());
  }

  @Test
  public void testLongNumberGeneration() {
    FakeOMatic   fake    = new FakeOMatic(new Random());
    Set<Long> numbers = new HashSet<>();

    for (int i = 0; i < 10000; i++) {
      long number = fake.number(1L, 50L);
      assertTrue(number >= 1L);
      assertTrue(number <= 50L);
      numbers.add(number);
    }

    assertEquals(50L, numbers.size());
  }

}
