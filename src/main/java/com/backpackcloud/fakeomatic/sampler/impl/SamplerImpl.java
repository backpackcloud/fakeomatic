package com.backpackcloud.fakeomatic.sampler.impl;

import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.fakeomatic.sampler.UndefinedSampleException;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

@RegisterForReflection
public class SamplerImpl implements Sampler {

  private final RandomGenerator random;
  private final Map<String, Sample> samples;
  private final Map<Character, String> placeholders;

  @JsonCreator
  public SamplerImpl(@JacksonInject RandomGenerator random,
                     @JsonProperty("samples") Map<String, Sample> samples,
                     @JsonProperty("placeholders") Map<Character, String> placeholders) {
    this.random = random;
    this.samples = Optional.ofNullable(samples).orElseGet(HashMap::new);
    this.placeholders = Optional.ofNullable(placeholders).orElseGet(HashMap::new);
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
  public void merge(Sampler sampler) {
    sampler.samples().forEach(this.samples::putIfAbsent);
    sampler.placeholders().forEach(this.placeholders::putIfAbsent);
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

  @Override
  public <E> E oneOf(List<? extends E> list) {
    return list.get(random.nextInt(list.size()));
  }

  @Override
  public <E> E oneOf(E... args) {
    return args[random.nextInt(args.length)];
  }

}
