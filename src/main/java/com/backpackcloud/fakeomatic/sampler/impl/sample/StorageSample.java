package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.random.RandomGenerator;

@RegisterForReflection
public class StorageSample implements Sample {

  public static final String TYPE = "storage";

  private final Sample sample;
  private final RandomGenerator random;
  private final Object[] storage;

  @JsonCreator
  public StorageSample(@JacksonInject Sample sample,
                       @JsonProperty("size") int size,
                       @JacksonInject RandomGenerator random) {
    this.sample = sample;
    this.storage = new Object[size];
    this.random = random;
  }

  @Override
  public Object get() {
    int index = random.nextInt(storage.length);
    Object stored = storage[index];
    if (stored == null) {
      stored = sample.get();
      storage[index] = stored;
    }
    return stored;
  }

}
