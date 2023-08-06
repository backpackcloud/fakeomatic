package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.fakeomatic.sampler.TemplateEval;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TemplateSample implements Sample<String> {

  public static final String TYPE = "template";

  private final Sample sample;
  private final TemplateEval templateEval;

  public TemplateSample(@JsonProperty("source") SampleConfiguration source,
                        @JsonProperty("value") String value,
                        @JacksonInject Sampler sampler) {
    this.sample = source != null ? source.sample() : Sample.of(value);
    this.templateEval = new TemplateEval(sampler);
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String get() {
    return templateEval.eval(sample.get().toString());
  }

}
