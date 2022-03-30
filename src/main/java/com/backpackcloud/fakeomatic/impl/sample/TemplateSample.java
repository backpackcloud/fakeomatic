package com.backpackcloud.fakeomatic.impl.sample;

import com.backpackcloud.fakeomatic.core.spi.Faker;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import com.backpackcloud.fakeomatic.impl.FakerResolver;
import com.backpackcloud.zipper.Configuration;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TemplateSample implements Sample<String> {

  private final TemplateInstance template;

  public TemplateSample(@JsonProperty("template") Configuration template,
                        @JacksonInject Faker faker) {
    this.template = Engine.builder()
      .addDefaults()
      .addValueResolver(new FakerResolver())
      .build()
      .parse(template.read())
      .data(faker);
  }

  @Override
  public String get() {
    return template.render();
  }

}
