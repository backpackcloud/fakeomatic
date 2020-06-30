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
import io.backpackcloud.fakeomatic.spi.Sample;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateSampleTest extends BaseTest {

  @Test
  public void testWithEndDate() {
    Faker             faker      = createFakeData("dates.yaml");
    Sample<LocalDate> sample     = faker.sample("2020");
    Set<Integer>      daysOfYear = new HashSet<>();
    times(100000, sample, date -> {
      assertEquals(2020, date.getYear());
      daysOfYear.add(date.getDayOfYear());
    });
    assertEquals(366, daysOfYear.size());
    times(366, day -> assertTrue(daysOfYear.contains(day)));
  }

  @Test
  public void testWithPeriod() {
    Faker             faker      = createFakeData("dates.yaml");
    Sample<LocalDate> sample     = faker.sample("quarter");
    Set<Integer>      daysOfYear = new HashSet<>();
    times(100000, sample, date -> {
      assertEquals(2020, date.getYear());
      daysOfYear.add(date.getDayOfYear());
    });
    assertEquals(91, daysOfYear.size());
    times(91, day -> assertTrue(daysOfYear.contains(day)));
  }

  @Test
  public void testYesterdayOrToday() {
    Faker             faker  = createFakeData("dates.yaml");
    Sample<LocalDate> sample = faker.sample("yesterday_or_today");
    Set<LocalDate>    dates  = new HashSet<>();
    times(10000, sample, dates::add);
    assertEquals(2, dates.size());
    assertTrue(dates.contains(LocalDate.now()));
    assertTrue(dates.contains(LocalDate.now().minusDays(1)));
  }

  @Test
  public void testYesterdayToTomorrow() {
    Faker             faker  = createFakeData("dates.yaml");
    Sample<LocalDate> sample = faker.sample("yesterday_to_tomorrow");
    Set<LocalDate>    dates  = new HashSet<>();
    times(10000, sample, dates::add);
    assertEquals(3, dates.size());
    assertTrue(dates.contains(LocalDate.now()));
    assertTrue(dates.contains(LocalDate.now().minusDays(1)));
    assertTrue(dates.contains(LocalDate.now().plusDays(1)));
  }

}
