package com.backpackcloud.fakeomatic.impl.sample;

import com.backpackcloud.fakeomatic.core.spi.Faker;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import com.backpackcloud.fakeomatic.core.spi.SampleConfiguration;
import com.backpackcloud.fakeomatic.impl.FakerResolver;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.qute.Engine;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TemplateSample implements Sample<String> {

  private final Sample sample;
  private final Faker faker;

  public TemplateSample(@JsonProperty("source") SampleConfiguration source,
                        @JacksonInject Faker faker) {
    this.sample = source.sample();
    this.faker = faker;
  }

  @Override
  public String get() {
    return Engine.builder()
      .addDefaults()
      .addValueResolver(new FakerResolver())
      .build()
      .parse(sample.get().toString())
      .data(faker)
      .render();
  }

}
