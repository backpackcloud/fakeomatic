package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.SampleConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RegisterForReflection
public class TimestampSample implements Sample<String> {

  public static final String TYPE = "timestamp";

  private LocalDateTime reference;
  private final Sample<Integer> accumulator;

  private final ChronoUnit unit;

  private final DateTimeFormatter formatter;

  public TimestampSample(LocalDateTime start,
                         Sample<Integer> accumulator,
                         ChronoUnit unit,
                         DateTimeFormatter formatter) {
    this.reference = start;
    this.accumulator = accumulator;
    this.unit = unit;
    this.formatter = formatter;
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String get() {
    reference = reference.plus(accumulator.get(), unit);
    return formatter.format(reference);
  }

  @JsonCreator
  public static TimestampSample create(@JsonProperty("from") SampleConfiguration from,
                                       @JsonProperty("accumulator") SampleConfiguration accumulator,
                                       @JsonProperty("unit") String unit,
                                       @JsonProperty("format") String formatString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Optional.ofNullable(formatString)
      .orElse("yyyy-MM-dd HH:mm:ss,SSS"));

    LocalDateTime start = (LocalDateTime) from.sample().get();

    return new TimestampSample(start, (Sample<Integer>) accumulator.sample(), ChronoUnit.valueOf(unit.toUpperCase()), formatter);
  }

}
