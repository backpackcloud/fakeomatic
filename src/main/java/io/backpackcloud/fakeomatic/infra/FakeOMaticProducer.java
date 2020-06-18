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
import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

@ApplicationScoped
public class FakeOMaticProducer {

  public static final String DEFAULT_CONFIG_LOCATION = "/META-INF/resources/config/fakeomatic.yaml";

  public static final String DEFAULT_CONFIG = "fakeomatic";

  private final Config.GeneratorConfig config;

  private final Vertx vertx;

  public FakeOMaticProducer(Config.GeneratorConfig config, Vertx vertx) {
    this.config = config;
    this.vertx = vertx;
  }

  @Produces
  @Singleton
  public FakeData produce() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    InjectableValues.Std std = new InjectableValues.Std();

    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    FakeData parent = new NullFakeData();
    // the composite sample needs access to the whole fake data and not the parent one
    RootFakeData rootFakeData = new RootFakeData();

    std.addValue(Random.class, config.random());
    std.addValue(Vertx.class, vertx);
    std.addValue("parent", parent);
    std.addValue("root", rootFakeData);
    objectMapper.setInjectableValues(std);

    for (String config : config.configs()) {
      if (DEFAULT_CONFIG.equals(config)) {
        parent = createDefault(objectMapper);
      } else {
        parent = createFromExternal(objectMapper, config);
      }
      std.addValue("parent", parent);
    }

    rootFakeData.delegate = parent;

    return parent;
  }

  private FakeOMatic createDefault(ObjectMapper objectMapper) throws IOException {
    return objectMapper.readValue(getClass().getResourceAsStream(DEFAULT_CONFIG_LOCATION), FakeOMatic.class);
  }

  private FakeOMatic createFromExternal(ObjectMapper objectMapper, String config) throws IOException {
    return objectMapper.readValue(new FileInputStream(new File(config)), FakeOMatic.class);
  }

  class RootFakeData implements FakeData {

    FakeData delegate;

    @Override
    public Random random() {
      return delegate.random();
    }

    @Override
    public Sample sample(String sampleName) {
      return delegate.sample(sampleName);
    }

    @Override
    public String randomFor(char placeholder) {
      return delegate.randomFor(placeholder);
    }

    @Override
    public String random(String sampleName) {
      return delegate.random(sampleName);
    }

    @Override
    public int number(int min, int max) {
      return delegate.number(min, max);
    }

    @Override
    public String expression(String expression) {
      return delegate.expression(expression);
    }

    @Override
    public String expressionFrom(String sampleName) {
      return delegate.expressionFrom(sampleName);
    }

    @Override
    public long number(long min, long max) {
      return delegate.number(min, max);
    }

  }

}
