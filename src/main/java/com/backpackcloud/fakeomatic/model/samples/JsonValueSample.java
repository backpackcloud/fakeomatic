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

package com.backpackcloud.fakeomatic.model.samples;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.fakeomatic.model.Sample;
import com.backpackcloud.fakeomatic.model.SampleConfiguration;
import com.backpackcloud.io.SerialBitter;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class JsonValueSample implements Sample<String> {

  public static final String TYPE = "json";

  private final SerialBitter serializer;
  private final Sample sample;
  private final String jsonPointer;

  public JsonValueSample(SerialBitter serializer, Sample sample, String jsonPointer) {
    this.serializer = serializer;
    this.sample = sample;
    this.jsonPointer = jsonPointer;
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String get() {
    try {
      Object data = sample.get();
      if (data instanceof JsonNode json) {
        return json.at(jsonPointer).asText();
      }
      String content = data.toString();
      JsonNode json = serializer.mapper().readTree(content);
      return json.at(jsonPointer).asText();
    } catch (Exception e) {
      throw new UnbelievableException(e);
    }
  }

  @JsonCreator
  public static JsonValueSample create(@JacksonInject SerialBitter serializer,
                                       @JsonProperty("path") String jsonPointer,
                                       @JsonProperty("source") SampleConfiguration source) {
    return new JsonValueSample(
      serializer,
      Optional.ofNullable(source)
        .map(SampleConfiguration::sample)
        .orElseThrow(UnbelievableException
          .because("No source was given.")),

      Optional.ofNullable(jsonPointer)
        .orElseThrow(UnbelievableException
          .because("No pointer was given."))
    );
  }

}
