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

package io.backpackcloud.fakeomatic.impl;

import io.backpackcloud.fakeomatic.spi.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Random;

import static io.backpackcloud.fakeomatic.impl.FakeOMaticProducer.DEFAULT_CONFIG;

@ApplicationScoped
public class FakeOMaticConfig implements Config {

  @ConfigProperty(name = "endpoint.url", defaultValue = "http://localhost:8080")
  String endpointUrl;

  @ConfigProperty(name = "endpoint.concurrency", defaultValue = "5")
  int concurrency;

  @ConfigProperty(name = "endpoint.insecure", defaultValue = "false")
  boolean insecure;

  @ConfigProperty(name = "generator.total", defaultValue = "10")
  int total;

  @ConfigProperty(name = "generator.buffer", defaultValue = "10")
  int buffer;

  @ConfigProperty(name = "generator.configs", defaultValue = DEFAULT_CONFIG)
  String configs;

  @ConfigProperty(name = "generator.seed", defaultValue = "")
  Random random;

  @ConfigProperty(name = "template.path", defaultValue = "./payload.json")
  String templatePath;

  @ConfigProperty(name = "template.type", defaultValue = "application/json; charset=UTF-8")
  String templateType;

  @ConfigProperty(name = "template.charset", defaultValue = "UTF-8")
  String templateCharset;

  @Produces
  @Override
  public EndpointConfig endpoint() {
    return new EndpointConfig() {
      @Override
      public String url() {
        return endpointUrl;
      }

      @Override
      public int concurrency() {
        return concurrency;
      }

      @Override
      public boolean insecure() {
        return insecure;
      }
    };
  }

  @Produces
  @Override
  public GeneratorConfig generator() {
    return new GeneratorConfig() {

      @Override
      public int buffer() {
        return buffer;
      }

      @Override
      public int total() {
        return total;
      }

      @Override
      public Random random() {
        return random;
      }

      @Override
      public String[] configs() {
        return configs.split("[,]");
      }
    };
  }

  @Produces
  @Override
  public TemplateConfig template() {
    return new TemplateConfig() {
      @Override
      public String path() {
        return templatePath;
      }

      @Override
      public String type() {
        return templateType;
      }

      @Override
      public String charset() {
        return templateCharset;
      }
    };
  }

}
