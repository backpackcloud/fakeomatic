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

package io.backpackcloud.fakeomatic.spi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.impl.configuration.ConfigurationDeserializer;
import io.backpackcloud.fakeomatic.impl.configuration.NotSuppliedConfiguration;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

@RegisterForReflection
@JsonDeserialize(using = ConfigurationDeserializer.class)
public interface Configuration extends Supplier<String> {

  Configuration NOT_SUPPLIED = new NotSuppliedConfiguration();

  boolean isSet();

  String get();

  default int getInt() {
    return Integer.parseInt(get());
  }

  default boolean getBoolean() {
    return Boolean.parseBoolean(get());
  }

  default String read() {
    try {
      return Files.readString(Path.of(get()));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  default List<String> readLines() {
    try {
      return Files.readAllLines(Path.of(get()));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  default String or(String defaultValue) {
    return isSet() ? get() : defaultValue;
  }

  default int or(int defaultValue) {
    return isSet() ? getInt() : defaultValue;
  }

  default boolean or(boolean defaultValue) {
    return isSet() ? getBoolean() : defaultValue;
  }

}
