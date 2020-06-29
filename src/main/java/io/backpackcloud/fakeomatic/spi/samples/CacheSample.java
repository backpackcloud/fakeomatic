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

package io.backpackcloud.fakeomatic.spi.samples;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.backpackcloud.fakeomatic.spi.Sample;

import java.util.Optional;

public class CacheSample implements Sample {

  private final Sample sample;
  private final int    ttl;

  private Object cachedValue;
  private int    hits;

  public CacheSample(Sample sample, int ttl) {
    this.sample = sample;
    this.ttl = ttl;
  }

  @Override
  public Object get() {
    if (this.cachedValue == null) {
      this.cachedValue = this.sample.get();
    }
    try {
      return this.cachedValue;
    } finally {
      if (++hits == ttl) {
        this.cachedValue = null;
      }
    }
  }

  @JsonCreator
  public static CacheSample create(@JacksonInject("root") FakeData fakeData,
                                   @JsonProperty("ref") String sampleName,
                                   @JsonProperty("sample") Sample sample,
                                   @JsonProperty("ttl") Integer ttl) {
    Integer timeToLive = Optional.ofNullable(ttl).orElse(Integer.MAX_VALUE);
    if (sample != null) {
      return new CacheSample(sample, timeToLive);
    } else if (sampleName != null) {
      return new CacheSample(fakeData.sample(sampleName), timeToLive);
    }
    throw new UnbelievableException("No sample or reference given");
  }

}
