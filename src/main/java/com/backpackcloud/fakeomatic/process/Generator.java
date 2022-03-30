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

package com.backpackcloud.fakeomatic.process;

import com.backpackcloud.fakeomatic.core.spi.Faker;
import com.backpackcloud.fakeomatic.core.spi.Sample;
import com.backpackcloud.fakeomatic.impl.FakerResolver;
import com.backpackcloud.fakeomatic.spi.Config;
import com.backpackcloud.fakeomatic.spi.FakeOMatic;
import io.quarkus.qute.Engine;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Generator implements QuarkusApplication {

  private final Config config;
  private final FakeOMatic fakeOMatic;

  public Generator(Config config, FakeOMatic fakeOMatic) {
    this.config = config;
    this.fakeOMatic = fakeOMatic;
  }

  @Override
  public int run(String... args) {
    Faker faker = fakeOMatic.faker();
    if (config.sample().isPresent()) {
      faker.sample(config.sample().get())
        .map(Sample::get)
        .ifPresentOrElse(System.out::println, () -> System.err.println("No sample found"));
    } else if (config.template().isPresent()) {
      String value = Engine.builder()
        .addDefaults()
        .addValueResolver(new FakerResolver())
        .build()
        .parse(config.template().get())
        .data(faker)
        .render();
      System.out.println(value);
    } else {
      System.err.println("No sample or template given");
      return 1;
    }
    return 0;
  }

}
