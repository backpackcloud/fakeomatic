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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.fakeomatic.Main;
import com.backpackcloud.fakeomatic.spi.Config;
import com.backpackcloud.sampler.Sample;
import com.backpackcloud.sampler.Sampler;
import com.backpackcloud.sampler.impl.SamplerBuilder;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
public class SamplerProducer {

  public static final String DEFAULT_CONFIG_LOCATION = "/META-INF/resources/config/fakeomatic.yml";

  public static final String DEFAULT_CONFIG = "fakeomatic";

  private final Config config;

  private final Vertx vertx;

  private final Engine templateEngine;

  public SamplerProducer(Config config, Vertx vertx, Engine templateEngine) {
    this.config = config;
    this.vertx = vertx;
    this.templateEngine = templateEngine;
  }

  @Produces
  @Singleton
  public Sampler produce() {
    SamplerBuilder builder = new SamplerBuilder(config.random());
    RootSampler rootSampler = new RootSampler();
    builder.inject(Vertx.class, this.vertx);
    builder.inject(Engine.class, this.templateEngine);
    builder.inject(Sampler.class, rootSampler);

    List<String> configurations = new ArrayList<>(Arrays.asList(config.configs()));
    Collections.reverse(configurations);

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
      .filter(Objects::nonNull)
      .forEach(builder::loadFrom);

    Sampler sampler = builder.build();
    rootSampler.delegate = sampler;
    return sampler;
  }

  public static InputStream defaultConfig() {
    return Main.class.getResourceAsStream(DEFAULT_CONFIG_LOCATION);
  }

  static class RootSampler implements Sampler {

    Sampler delegate;

    @Override
    public List<Sample> samples() {
      return delegate.samples();
    }

    @Override
    public Optional<Sample> sample(String sampleName) {
      return Optional.ofNullable(() ->
        delegate.sample(sampleName)
          .orElseThrow(UnbelievableException::new)
          .get()
      );
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
