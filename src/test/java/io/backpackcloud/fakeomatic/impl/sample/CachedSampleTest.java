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

package io.backpackcloud.fakeomatic.impl.sample;

import io.backpackcloud.fakeomatic.BaseTest;
import io.backpackcloud.fakeomatic.spi.FakeOMatic;
import io.backpackcloud.fakeomatic.spi.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CachedSampleTest extends BaseTest {

  FakeOMatic fakeOMatic;

  @BeforeEach
  public void init() {
    fakeOMatic = createFakeOMatic("cache.yaml");
  }

  @Test
  public void testCachedSample() {
    Sample<UUID> sample = fakeOMatic.sample("cached_uuid");
    UUID         uuid   = sample.get();
    times(100, sample, value -> assertSame(uuid, value));
  }

  @Test
  public void testTtlCachedSample() {
    Sample<UUID> sample = fakeOMatic.sample("ttl_cached_uuid");
    UUID         first  = sample.get();
    times(99, sample, value -> assertSame(first, value));
    UUID second = sample.get();
    assertNotSame(first, second);
    times(99, sample, value -> assertSame(second, value));
  }

}
