package com.backpackcloud.fakeomatic.model.samples;

import com.backpackcloud.fakeomatic.model.Sample;
import com.backpackcloud.fakeomatic.model.SampleConfiguration;
import com.backpackcloud.fakeomatic.model.Sampler;
import com.backpackcloud.fakeomatic.model.TemplateEvaluator;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplateSample implements Sample<String> {

  public static final String TYPE = "template";

  private final Sample template;
  private final TemplateEvaluator templateEvaluator;

  public TemplateSample(Sampler sampler, Sample template) {
    this.template = template;
    this.templateEvaluator = new TemplateEvaluator(sampler);
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String get() {
    return templateEvaluator.eval(template.get().toString());
  }

  @JsonCreator
  public static TemplateSample create(@JsonProperty("source") SampleConfiguration source,
                                      @JsonProperty("value") String value,
                                      @JacksonInject Sampler sampler) {
    Sample template = source != null ? source.sample() : Sample.of(value);
    return new TemplateSample(sampler, template);
  }

}
