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

package io.backpackcloud.fakeomatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.backpackcloud.fakeomatic.impl.FakeOMaticProducer;
import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.qute.Engine;
import io.vertx.mutiny.core.Vertx;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseTest {

  protected Config                 config;
  protected Config.TemplateConfig  templateConfig;
  protected Config.GeneratorConfig generatorConfig;
  protected Config.EndpointConfig  endpointConfig;
  protected Random                 random;
  protected ObjectMapper           objectMapper;

  @BeforeEach
  public void _init() {
    random = new Random();
    objectMapper = new ObjectMapper(new YAMLFactory());

    config = mock(Config.class);
    templateConfig = mock(Config.TemplateConfig.class);
    generatorConfig = mock(Config.GeneratorConfig.class);
    endpointConfig = mock(Config.EndpointConfig.class);

    when(config.template()).thenReturn(templateConfig);
    when(config.generator()).thenReturn(generatorConfig);
    when(config.endpoint()).thenReturn(endpointConfig);

    when(generatorConfig.random()).thenReturn(random);
  }

  protected void times(int times, Consumer<Integer> consumer) {
    for (int i = 0; i < times; i++) {
      consumer.accept(i);
    }
  }

  protected void times(int times, Runnable runnable) {
    for (int i = 0; i < times; i++) {
      runnable.run();
    }
  }

  protected <E> void times(int times, Sample<E> sample, Consumer<E> consumer) {
    times(times, () -> consumer.accept(sample.get()));
  }

  protected FakeData createFakeData(String... names) {
    String path = getClass().getPackageName().replaceAll("\\.", "/");
    List<String> configs = Arrays.stream(names)
                                 .map(name -> "src/test/resources/" + path + "/" + name)
                                 .collect(Collectors.toList());

    when(generatorConfig.configs()).thenReturn(configs.toArray(new String[configs.size()]));

    FakeOMaticProducer producer = new FakeOMaticProducer(
        config,
        new Vertx(mock(io.vertx.core.Vertx.class)),
        Engine.builder().addDefaults().build()
    );
    return producer.produce();
  }

}
