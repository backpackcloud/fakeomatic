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
import io.backpackcloud.fakeomatic.impl.CompositeConfigurationValue;
import io.backpackcloud.fakeomatic.impl.EnvironmentVariableValue;
import io.backpackcloud.fakeomatic.impl.FileContentValue;
import io.backpackcloud.fakeomatic.impl.RawValue;
import io.backpackcloud.fakeomatic.impl.ResourceValue;
import io.backpackcloud.fakeomatic.impl.SystemPropertyValue;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RegisterForReflection
public interface ConfigurationValue extends Supplier<String> {

  boolean isSet();

  String get();

  @JsonCreator
  static ConfigurationValue create(String value) {
    return new RawValue(value);
  }

  @JsonCreator
  static ConfigurationValue create(@JsonProperty("env") String env,
                                   @JsonProperty("property") String property,
                                   @JsonProperty("file") String file,
                                   @JsonProperty("resource") String resource,
                                   @JsonProperty("default") String defaultValue) {
    List<ConfigurationValue> values = new ArrayList<>();

    if (env != null) {
      values.add(new EnvironmentVariableValue(env));
    }
    if (property != null) {
      values.add(new SystemPropertyValue(property));
    }
    if (file != null) {
      values.add(new FileContentValue(file));
    }
    if (resource != null) {
      values.add(new ResourceValue(resource));
    }
    if (defaultValue != null) {
      values.add(new RawValue(defaultValue));
    }

    return new CompositeConfigurationValue(values);
  }

}
