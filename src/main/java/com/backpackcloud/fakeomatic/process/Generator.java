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
import io.quarkus.qute.Engine;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class Generator implements QuarkusApplication {

  private final Faker faker;

  public Generator(Faker faker) {
    this.faker = faker;
  }

  @Override
  public int run(String... args) throws Exception {
    Mode mode = Mode.valueOf(args[0].toUpperCase());
    String value = args[1];

    switch (mode) {
      case SAMPLE:
        faker.sample(value)
          .map(Sample::get)
          .ifPresentOrElse(System.out::println, () -> System.err.println("No sample found"));
        break;
      case TEMPLATE:
        String render = Engine.builder()
          .addDefaults()
          .addValueResolver(new FakerResolver())
          .build()
          .parse(value)
          .data(faker)
          .render();
        System.out.println(render);
        break;
      case EXPRESSION:
        System.out.println(faker.expression(value));
    }
    return 0;
  }

  public enum Mode {

    SAMPLE, TEMPLATE, EXPRESSION

  }

}
