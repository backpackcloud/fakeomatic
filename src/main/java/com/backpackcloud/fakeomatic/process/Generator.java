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

import com.backpackcloud.sampler.Sample;
import com.backpackcloud.sampler.Sampler;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Generator implements QuarkusApplication {

  private final Sampler sampler;

  public Generator(Sampler sampler) {
    this.sampler = sampler;
  }

  @Override
  public int run(String... args) {
    Mode mode = Mode.valueOf(args[0].toUpperCase());
    String value = args[1];

    switch (mode) {
      case SAMPLE:
        sampler.sample(value)
          .map(Sample::get)
          .ifPresentOrElse(System.out::println, () -> System.err.println("No sample found"));
        break;
      case TEMPLATE:
        System.out.println(sampler.interpolator().apply(value));
        break;
      case EXPRESSION:
        System.out.println(sampler.expression(value));
    }
    return 0;
  }

  public enum Mode {

    SAMPLE, TEMPLATE, EXPRESSION

  }

}
