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

package io.backpackcloud.fakeomatic.impl.configuration;

import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Configuration;

import java.util.List;
import java.util.function.Function;

public class CompositeConfiguration implements Configuration {

  private final List<Configuration> values;

  public CompositeConfiguration(List<Configuration> values) {
    this.values = values;
  }

  @Override
  public boolean isSet() {
    return values.stream()
                 .anyMatch(Configuration::isSet);
  }

  @Override
  public String get() {
    return first(Configuration::get);
  }

  @Override
  public String read() {
    return first(Configuration::read);
  }

  @Override
  public List<String> readLines() {
    return first(Configuration::readLines);
  }

  private <T> T first(Function<Configuration, T> mapper) {
    return values.stream()
                 .filter(Configuration::isSet)
                 .findFirst()
                 .map(mapper)
                 .orElseThrow(UnbelievableException::new);
  }
}
