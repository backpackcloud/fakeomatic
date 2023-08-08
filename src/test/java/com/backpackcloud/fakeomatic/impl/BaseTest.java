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

package com.backpackcloud.fakeomatic.impl;

import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;

import java.util.Random;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

public abstract class BaseTest {

  protected void times(int times, Consumer<Integer> consumer) {
    for (int i = 1; i <= times; i++) {
      consumer.accept(i);
    }
  }

  protected void times(int times, Runnable runnable) {
    times(times, integer -> runnable.run());
  }

  protected <E> void times(int times, Sample<E> sample, Consumer<E> consumer) {
    times(times, () -> consumer.accept(sample.get()));
  }

  protected Sampler createSampler(String name) {
    return createSampler(name, new Random());
  }

  protected Sampler createSampler(String name, RandomGenerator random) {
    String path = getClass().getPackageName().replaceAll("\\.", "/");
    return Sampler.loadFrom(new ResourceConfiguration(path + "/" + name), random);
  }

}
