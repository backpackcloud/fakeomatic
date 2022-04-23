package com.backpackcloud.fakeomatic.sampler.impl;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.fakeomatic.sampler.UndefinedSampleException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegisterForReflection
public class SamplerImpl implements Sampler {

  private final Map<String, Sample> samples;
  private final Map<Character, String> placeholders;

  @JsonCreator
  public SamplerImpl(@JsonProperty("samples") Map<String, Sample> samples,
                     @JsonProperty("placeholders") Map<Character, String> placeholders) {
    this.samples = Optional.ofNullable(samples).orElseGet(Collections::emptyMap);
    this.placeholders = Optional.ofNullable(placeholders).orElseGet(Collections::emptyMap);
  }

  @Override
  public Map<String, Sample> samples() {
    return new HashMap<>(this.samples);
  }

  @Override
  public Map<Character, String> placeholders() {
    return new HashMap<>(this.placeholders);
  }

  @Override
  public <E> Optional<Sample<E>> sample(String sampleName) {
    if (samples.containsKey(sampleName)) {
      return Optional.of(samples.get(sampleName));
    }
    return Optional.empty();
  }

  @Override
  public String some(char placeholder) {
    if (placeholders.containsKey(placeholder)) {
      return sample(placeholders.get(placeholder))
        .map(Sample::get)
        .map(Object::toString)
        .orElseThrow(UndefinedSampleException::new);
    }
    return String.valueOf(placeholder);
  }


}
