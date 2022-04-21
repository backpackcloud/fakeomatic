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

import com.backpackcloud.impl.configuration.FileConfiguration;
import com.backpackcloud.sampler.Sampler;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class SamplerProducer {

  @ConfigProperty(name = "generator.seed")
  Optional<Random> random;

  @ConfigProperty(name = "generator.config")
  Optional<String> config;

  @Produces
  @Singleton
  public Sampler produce() {
    Random random = this.random.orElse(new Random());
    return config.map(FileConfiguration::new)
      .map(file -> Sampler.loadFrom(file, random))
      .orElseGet(() -> Sampler.defaultSampler(random));
  }

}