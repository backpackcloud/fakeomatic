/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.backpackcloud.fakeomatic.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.backpackcloud.fakeomatic.Config;
import io.backpackcloud.fakeomatic.impl.FakeOMatic;
import io.backpackcloud.fakeomatic.impl.NullFakeData;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class FakeOMaticProducer {

  public static final String DEFAULT_CONFIG_LOCATION = "/META-INF/resources/config/fakeomatic.yaml";

  public static final String DEFAULT_CONFIG = "fakeomatic";

  private final Config config;

  private final Vertx vertx;

  public FakeOMaticProducer(Config config, Vertx vertx) {
    this.config = config;
    this.vertx = vertx;
  }

  @Produces
  @Singleton
  public FakeData produce() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    InjectableValues.Std std = new InjectableValues.Std();

    std.addValue(Random.class, config.random());
    std.addValue(Vertx.class, vertx);

    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.setInjectableValues(std);

    String[] configLocations = config.configs().split("[,]");

    FakeData fakeData = new NullFakeData();
    std.addValue(FakeData.class, fakeData);

    for (String config : configLocations) {
      if (DEFAULT_CONFIG.equals(config)) {
        fakeData = createDefault(objectMapper);
      } else {
        fakeData = createFromExternal(objectMapper, config);
      }
      std.addValue(FakeData.class, fakeData);
    }

    return fakeData;
  }

  private FakeOMatic createDefault(ObjectMapper objectMapper) throws IOException {
    return objectMapper.readValue(getClass().getResourceAsStream(DEFAULT_CONFIG_LOCATION), FakeOMatic.class)
                       .addSample("uuid", rand -> UUID.randomUUID().toString());
  }

  private FakeOMatic createFromExternal(ObjectMapper objectMapper, String config) throws IOException {
    return objectMapper.readValue(new FileInputStream(new File(config)), FakeOMatic.class);
  }

}
