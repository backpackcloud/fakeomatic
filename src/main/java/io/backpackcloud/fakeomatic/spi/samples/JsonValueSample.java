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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Faker;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class JsonValueSample implements Sample<String> {

  private final ObjectMapper objectMapper;
  private final Sample       sample;
  private final String       jsonPointer;

  public JsonValueSample(ObjectMapper objectMapper, Sample sample, String jsonPointer) {
    this.objectMapper = objectMapper;
    this.sample = sample;
    this.jsonPointer = jsonPointer;
  }

  @Override
  public String get() {
    try {
      String   content  = sample.get().toString();
      JsonNode jsonNode = objectMapper.readTree(content);
      return jsonNode.at(jsonPointer).asText();
    } catch (JsonProcessingException e) {
      throw new UnbelievableException(e);
    }
  }

  @JsonCreator
  public static JsonValueSample create(@JacksonInject("root") Faker faker,
                                       @JsonProperty("path") String jsonPointer,
                                       @JsonProperty("ref") String sampleName,
                                       @JsonProperty("sample") Sample sample) {
    return new JsonValueSample(new ObjectMapper(), sampleName != null ? faker.sample(sampleName) : sample, jsonPointer);
  }

}
