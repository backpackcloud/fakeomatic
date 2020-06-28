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

package io.backpackcloud.fakeomatic.spi.samples;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@RegisterForReflection
public class DateSample implements Sample<LocalDate> {

  private final Random    random;
  private final LocalDate start;
  private final int       maxDays;

  public DateSample(Random random, LocalDate start, int maxDays) {
    this.random = random;
    this.start = start;
    this.maxDays = maxDays;
  }

  @Override
  public LocalDate get() {
    return start.plus(random.nextInt(maxDays), ChronoUnit.DAYS);
  }

  @JsonCreator
  public static DateSample create(@JacksonInject Random random,
                                  @JsonProperty("from") String fromDate,
                                  @JsonProperty("to") String toDate,
                                  @JsonProperty("format") String formatString) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern(Optional.ofNullable(formatString).orElse("yyyy-MM-dd"));

    LocalDate start  = LocalDate.parse(fromDate, format);
    LocalDate end    = LocalDate.parse(toDate, format);
    Period    period = Period.between(start, end);

    return new DateSample(random, start, period.getDays());
  }

}
