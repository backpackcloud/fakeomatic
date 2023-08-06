package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.backpackcloud.serializer.Serializer;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class JsonSample implements Sample<JsonNode> {

  public static final String TYPE = "json";

  private final Serializer serializer;
  private final Sample sample;

  public JsonSample(@JacksonInject Serializer serializer,
                    @JsonProperty("source") SampleConfiguration source) {
    this.serializer = serializer;
    this.sample = source.sample();
  }

  @Override
  public JsonNode get() {
    try {
      return serializer.mapper().readTree(sample.get().toString());
    } catch (JsonProcessingException e) {
      throw new UnbelievableException(e);
    }
  }

}
