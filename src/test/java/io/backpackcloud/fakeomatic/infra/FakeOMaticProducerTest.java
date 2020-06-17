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

package io.backpackcloud.fakeomatic.infra;

import io.backpackcloud.fakeomatic.BaseTest;
import io.backpackcloud.fakeomatic.spi.FakeData;
import io.vertx.mutiny.core.Vertx;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FakeOMaticProducerTest extends BaseTest {

  @Test
  public void testCompositeSampleCreation() throws IOException {
    Random random = new Random();

    when(generatorConfig.configs()).thenReturn(new String[]{"src/test/java/io/backpackcloud/fakeomatic/infra/test.yaml", "fakeomatic"});
    when(generatorConfig.random()).thenReturn(random);

    FakeOMaticProducer producer = new FakeOMaticProducer(config, new Vertx(mock(io.vertx.core.Vertx.class)));

    FakeData fakeData = producer.produce();

    fakeData.random("char");
  }

}
