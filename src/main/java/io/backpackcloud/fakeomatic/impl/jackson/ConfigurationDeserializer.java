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

package io.backpackcloud.fakeomatic.impl.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.backpackcloud.fakeomatic.impl.configuration.EnvironmentVariableConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.FileContentConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.RawValueConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.ResourceConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.SystemPropertyConfiguration;
import io.backpackcloud.fakeomatic.impl.configuration.UrlConfiguration;
import io.backpackcloud.fakeomatic.spi.Configuration;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@RegisterForReflection
public class ConfigurationDeserializer extends JsonDeserializer<Configuration> {

  @Override
  public Configuration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    List<Configuration> configurations = new ArrayList<>();
    JsonNode            jsonNode       = ctxt.readTree(p);

    ifNotBlank(jsonNode, "/env", EnvironmentVariableConfiguration::new, configurations::add);

    ifNotBlank(jsonNode, "/property", SystemPropertyConfiguration::new, configurations::add);
    ifNotBlank(jsonNode, "/file", FileContentConfiguration::new, configurations::add);
    ifNotBlank(jsonNode, "/resource", ResourceConfiguration::new, configurations::add);
    ifNotBlank(jsonNode, "/url", UrlConfiguration::new, configurations::add);
    ifNotBlank(jsonNode, "/value", RawValueConfiguration::new, configurations::add);
    ifNotBlank(jsonNode, "", RawValueConfiguration::new, configurations::add);

    return configurations.stream()
                         .filter(Configuration::isSet)
                         .findFirst()
                         .orElse(Configuration.NOT_SUPPLIED);
  }

  private void ifNotBlank(JsonNode jsonNode,
                          String pointer,
                          Function<String, Configuration> function,
                          Consumer<Configuration> consumer) {
    String value = jsonNode.at(pointer).asText();
    if (!value.isBlank()) {
      consumer.accept(function.apply(value));
    }
  }

  @Override
  public Configuration getNullValue(DeserializationContext ctxt) {
    return Configuration.NOT_SUPPLIED;
  }

}
