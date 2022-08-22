package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RegisterForReflection
public class FormatterSample implements Sample<String> {

  public static final String TYPE = "formatter";

  private final Sample<LocalDateTime> sample;
  private final DateTimeFormatter formatter;

  public FormatterSample(Sample<LocalDateTime> sample, DateTimeFormatter formatter) {
    this.sample = sample;
    this.formatter = formatter;
  }

  @Override
  public String get() {
    return formatter.format(sample.get());
  }

  @Override
  public String type() {
    return TYPE;
  }

  @JsonCreator
  public static FormatterSample create(@JsonProperty("source") SampleConfiguration reference,
                                       @JsonProperty("format") String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Optional.ofNullable(format)
      .orElse("yyyy-MM-dd HH:mm:ss,SSS"));

    return new FormatterSample((Sample<LocalDateTime>) reference.sample(), formatter);
  }

}
