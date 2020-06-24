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
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RegisterForReflection
public class ExternalizableValue {

  private final String value;

  @JsonCreator
  public ExternalizableValue(String value) {
    this.value = value;
  }

  public String get() {
    return value;
  }

  @JsonCreator
  public static ExternalizableValue create(@JsonProperty("env") String env,
                                           @JsonProperty("property") String property,
                                           @JsonProperty("file") String file,
                                           @JsonProperty("resource") String resource,
                                           @JsonProperty("default") String defaultValue) {
    if (env != null) return new ExternalizableValue(getOrDefault(System.getenv(env), defaultValue));
    else if (property != null) return new ExternalizableValue(getOrDefault(System.getProperty(property), defaultValue));
    else if (file != null) {
      try {
        return new ExternalizableValue(getOrDefault(Files.readString(Path.of(file)), defaultValue));
      } catch (IOException e) {
        throw new UnbelievableException(e);
      }
    } else if (resource != null) {
      InputStream inputStream = ExternalizableValue.class.getResourceAsStream(resource);
      try (inputStream) {
        return new ExternalizableValue(getOrDefault(new String(inputStream.readAllBytes()), defaultValue));
      } catch (IOException e) {
        throw new UnbelievableException(e);
      }
    } else throw new UnbelievableException("Unable to populate value");
  }

  @JsonCreator
  public static ExternalizableValue create(List<ExternalizableValue> values) {
    return values.stream().filter(Objects::nonNull).findFirst().orElseThrow(UnbelievableException::new);
  }

  private static String getOrDefault(String value, String defaultValue) {
    return value == null ? defaultValue : value;
  }

}
