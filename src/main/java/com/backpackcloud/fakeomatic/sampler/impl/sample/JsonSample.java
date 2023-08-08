package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.backpackcloud.serializer.Serializer;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class JsonSample implements Sample<Map> {

  public static final String TYPE = "json";

  private final Serializer serializer;
  private final Sample sample;

  public JsonSample(@JacksonInject Serializer serializer,
                    @JsonProperty("source") SampleConfiguration source) {
    this.serializer = serializer;
    this.sample = source.sample();
  }

  @Override
  public Map get() {
    try {
      return serializer.mapper().readValue(sample.get().toString(), Map.class);
    } catch (JsonProcessingException e) {
      throw new UnbelievableException(e);
    }
  }

}
