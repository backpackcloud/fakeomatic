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

import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.fakeomatic.sampler.TemplateEval;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Generator implements QuarkusApplication {

  private final Sampler sampler;
  private final int count;

  public Generator(Sampler sampler,
                   @ConfigProperty(name = "generator.count", defaultValue = "1") int count) {
    this.sampler = sampler;
    this.count = count;
  }

  @Override
  public int run(String... args) {
    Mode mode = Mode.valueOf(args[0].toUpperCase());
    String value = args[1];

    for (int i = 0; i < count; i++) {
      switch (mode) {
        case SAMPLE -> System.out.println(sampler.some(value).toString());
        case TEMPLATE -> System.out.println(new TemplateEval(sampler).eval(value));
        case EXPRESSION -> System.out.println(sampler.expression(value));
      }
    }
    return 0;
  }

  public enum Mode {

    SAMPLE, TEMPLATE, EXPRESSION

  }

}
