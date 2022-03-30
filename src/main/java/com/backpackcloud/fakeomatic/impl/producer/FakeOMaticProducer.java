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

package com.backpackcloud.fakeomatic.impl.producer;

import com.backpackcloud.fakeomatic.core.impl.FakerBuilder;
import com.backpackcloud.fakeomatic.core.spi.Faker;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import com.backpackcloud.fakeomatic.impl.FakeOMaticImpl;
import com.backpackcloud.fakeomatic.impl.sample.ApiSample;
import com.backpackcloud.fakeomatic.impl.sample.TemplateSample;
import com.backpackcloud.fakeomatic.spi.Config;
import com.backpackcloud.fakeomatic.spi.Endpoint;
import com.backpackcloud.fakeomatic.spi.FakeOMatic;
import com.backpackcloud.zipper.UnbelievableException;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class FakeOMaticProducer {

  private static final Logger LOGGER = Logger.getLogger(FakeOMaticProducer.class);

  public static final String DEFAULT_CONFIG_LOCATION = "/META-INF/resources/config/fakeomatic.yaml";

  public static final String DEFAULT_CONFIG = "fakeomatic";

  private final Config config;

  private final Vertx vertx;

  private final Engine templateEngine;

  public FakeOMaticProducer(Config config, Vertx vertx, Engine templateEngine) {
    this.config = config;
    this.vertx = vertx;
    this.templateEngine = templateEngine;
  }

  @Produces
  @Singleton
  public FakeOMatic produce() {
    FakerBuilder builder = new FakerBuilder(config.random());
    RootFaker rootFaker = new RootFaker();
    builder.inject(Vertx.class, this.vertx);
    builder.inject(Engine.class, this.templateEngine);
    builder.inject(Faker.class, rootFaker);
    builder.register("api", ApiSample.class);
    builder.register("template", TemplateSample.class);

    List<String> configurations = new ArrayList<>(Arrays.asList(config.configs()));
    Collections.reverse(configurations);

    Consumer<InputStream> loadFaker   = builder::loadFrom;
    Map<String, Endpoint> endpointMap = new HashMap<>();

    Function<String, InputStream> convertToInputStream = config -> {
      try {
        if (DEFAULT_CONFIG.equals(config)) {
          return defaultConfig();
        } else {
          return new FileInputStream(config);
        }
      } catch (FileNotFoundException e) {
        throw new UnbelievableException(e);
      }
    };
    configurations.stream()
                  .map(convertToInputStream)
                  .forEach(loadFaker);

    Faker faker = builder.build();
    rootFaker.delegate = faker;
    return new FakeOMaticImpl(endpointMap, faker);
  }

  public static InputStream defaultConfig() {
    return FakeOMaticImpl.class.getResourceAsStream(DEFAULT_CONFIG_LOCATION);
  }

  static class RootFaker implements Faker {

    Faker delegate;

    @Override
    public List<Sample> samples() {
      return delegate.samples();
    }

    @Override
    public Optional<Sample> sample(String sampleName) {
      return Optional.ofNullable(() -> delegate.sample(sampleName).orElseThrow(UnbelievableException::new).get());
    }

    @Override
    public String some(char placeholder) {
      return delegate.some(placeholder);
    }

    @Override
    public <E> E some(String sampleName) {
      return delegate.some(sampleName);
    }

    @Override
    public String expression(String expression) {
      return delegate.expression(expression);
    }

  }

}
