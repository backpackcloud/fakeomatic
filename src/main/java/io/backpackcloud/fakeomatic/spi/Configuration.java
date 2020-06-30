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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.impl.configuration.CompositeConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.EnvironmentVariableConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.FileContentConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.RawValueConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.ResourceConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.SystemPropertyConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.UrlConfiguration;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RegisterForReflection
public interface Configuration extends Supplier<String> {

  boolean isSet();

  String get();

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

  @JsonCreator
  static Configuration create(String value) {
    return new RawValueConfiguration(value);
  }

  @JsonCreator
  static Configuration create(@JsonProperty("env") String env,
                              @JsonProperty("property") String property,
                              @JsonProperty("file") String file,
                              @JsonProperty("resource") String resource,
                              @JsonProperty("url") String url,
                              @JsonProperty("default") String defaultValue) {
    List<Configuration> values = new ArrayList<>();

    if (env != null) {
      values.add(new EnvironmentVariableConfiguration(env));
    }
    if (property != null) {
      values.add(new SystemPropertyConfiguration(property));
    }
    if (file != null) {
      values.add(new FileContentConfiguration(file));
    }
    if (resource != null) {
      values.add(new ResourceConfiguration(resource));
    }
    if (url != null) {
      values.add(new UrlConfiguration(url));
    }
    if (defaultValue != null) {
      values.add(new RawValueConfiguration(defaultValue));
    }

    return new CompositeConfiguration(values);
  }

}
