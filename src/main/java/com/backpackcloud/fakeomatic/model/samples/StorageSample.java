package com.backpackcloud.fakeomatic.model.samples;

import com.backpackcloud.fakeomatic.model.Sample;
import com.backpackcloud.fakeomatic.model.SampleConfiguration;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.random.RandomGenerator;

public class StorageSample implements Sample {

  public static final String TYPE = "storage";

  private final Sample sample;
  private final RandomGenerator random;
  private final Object[] storage;

  @JsonCreator
  public StorageSample(@JsonProperty("source") SampleConfiguration source,
                       @JsonProperty("size") int size,
                       @JacksonInject RandomGenerator random) {
    this.sample = source.sample();
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
