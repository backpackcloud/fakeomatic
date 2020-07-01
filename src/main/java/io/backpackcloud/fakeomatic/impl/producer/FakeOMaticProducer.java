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

package io.backpackcloud.fakeomatic.impl.producer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.impl.FakeOMaticImpl;
import io.backpackcloud.fakeomatic.impl.NullFakeOMatic;
import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.backpackcloud.fakeomatic.spi.FakeOMatic;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    List<InputStream> configs = Arrays
        .stream(config.configs())
        .map(config -> {
          try {
            if (DEFAULT_CONFIG.equals(config)) {
              return defaultConfig();
            } else {
              return new FileInputStream(new File(config));
            }
          } catch (FileNotFoundException e) {
            throw new UnbelievableException(e);
          }
        })
        .collect(Collectors.toList());
    return newInstance(configs, templateEngine, std -> {
      std.addValue(Random.class, this.config.random());
      std.addValue(Vertx.class, this.vertx);
      std.addValue(Config.class, this.config);
    });
  }

  public static InputStream defaultConfig() {
    return FakeOMaticImpl.class.getResourceAsStream(DEFAULT_CONFIG_LOCATION);
  }

  public static FakeOMatic newInstance(List<InputStream> configs,
                                       Engine engine,
                                       Consumer<InjectableValues.Std> injectableValuesConsumer) {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    InjectableValues.Std std = new InjectableValues.Std();

    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    FakeOMatic parent = new NullFakeOMatic();
    // the composite sample needs access to the whole fake data and not the parent one
    RootFakeOMatic root = new RootFakeOMatic();

    injectableValuesConsumer.accept(std);
    std.addValue("parent", parent);
    std.addValue("root", root);
    std.addValue(Engine.class, engine);
    objectMapper.setInjectableValues(std);

    try {
      for (InputStream config : configs) {
        parent = objectMapper.readValue(config, FakeOMaticImpl.class);
        std.addValue("parent", parent);
      }
    } catch (Throwable e) {
      LOGGER.error("Error while parsing configuration", e);
      throw new UnbelievableException(e);
    }

    root.delegate = parent;

    return parent;
  }

  static class RootFakeOMatic implements FakeOMatic {

    FakeOMatic delegate;

    @Override
    public Optional<Endpoint> endpoint(String name) {
      return delegate.endpoint(name);
    }

    @Override
    public Random random() {
      return delegate.random();
    }

    @Override
    public Sample sample(String sampleName) {
      // wait before passing the real sample because the hierarchy is being updated
      // as the objects is being constructed so this enables samples to have a reference
      // for other samples instead of having to depend on the FakeData instance
      return () -> delegate.sample(sampleName).get();
    }

    @Override
    public String some(char placeholder) {
      return delegate.some(placeholder);
    }

    @Override
    public Object some(String sampleName) {
      return delegate.some(sampleName);
    }

    @Override
    public String expression(String expression) {
      return delegate.expression(expression);
    }

    @Override
    public List<Sample> samples() {
      return delegate.samples();
    }
  }

}
