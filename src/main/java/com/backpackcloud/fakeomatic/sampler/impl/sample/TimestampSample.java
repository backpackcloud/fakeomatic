package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RegisterForReflection
public class TimestampSample implements Sample<LocalDateTime> {

  public static final String TYPE = "timestamp";

  private LocalDateTime reference;
  private final Sample<Integer> accumulator;

  private final ChronoUnit unit;

  public TimestampSample(LocalDateTime start,
                         Sample<Integer> accumulator,
                         ChronoUnit unit) {
    this.reference = start;
    this.accumulator = accumulator;
    this.unit = unit;
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public LocalDateTime get() {
    return reference = reference.plus(accumulator.get(), unit);
  }

  @JsonCreator
  public static TimestampSample create(@JsonProperty("from") SampleConfiguration from,
                                       @JsonProperty("accumulator") SampleConfiguration accumulator,
                                       @JsonProperty("unit") String unit) {
    LocalDateTime start = (LocalDateTime) from.sample().get();

    return new TimestampSample(start, (Sample<Integer>) accumulator.sample(), ChronoUnit.valueOf(unit.toUpperCase()));
  }

}
