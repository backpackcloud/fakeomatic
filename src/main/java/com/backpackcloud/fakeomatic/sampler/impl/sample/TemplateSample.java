package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.function.Function;

@RegisterForReflection
public class TemplateSample implements Sample<String> {

  public static final String TYPE = "template";

  private final Sample sample;
  private final Function<String, String> interpolator;

  public TemplateSample(@JsonProperty("source") SampleConfiguration source,
                        @JacksonInject Sampler sampler) {
    this.sample = source.sample();
    this.interpolator = sampler.interpolator();
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String get() {
    return interpolator.apply(sample.get().toString());
  }

}
