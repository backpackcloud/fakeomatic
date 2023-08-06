package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RegisterForReflection
public class SequenceSample implements Sample<Long> {

  public static final String TYPE = "sequence";

  private final AtomicLong count;
  private final long step;

  public SequenceSample(@JsonProperty("start") Long start,
                        @JsonProperty("step") Long step) {
    this.count = Optional.ofNullable(start)
      .map(AtomicLong::new)
      .orElseGet(AtomicLong::new);
    this.step = Optional.ofNullable(step).orElse(1L);
  }

  @Override
  public Long get() {
    return count.getAndAdd(step);
  }

  @Override
  public String type() {
    return TYPE;
  }

}
