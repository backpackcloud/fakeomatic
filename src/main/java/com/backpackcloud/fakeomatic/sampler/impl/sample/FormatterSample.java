package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class FormatterSample implements Sample<String> {

  public static final String TYPE = "formatter";

  private final Sample<?> sample;
  private final String format;

  public FormatterSample(Sample<?> sample, String format) {
    this.sample = sample;
    this.format = format;
  }

  @Override
  public String get() {
    return String.format(format, sample.get());
  }

  @Override
  public String type() {
    return TYPE;
  }

  @JsonCreator
  public static FormatterSample create(@JsonProperty("sample") SampleConfiguration reference,
                                       @JsonProperty("format") String format) {
    return new FormatterSample(reference.sample(), format);
  }

}
