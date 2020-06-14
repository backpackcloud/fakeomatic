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

import io.backpackcloud.fakeomatic.spi.Config;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateProducer {

  private final Config config;

  public TemplateProducer(Config config) {
    this.config = config;
  }

  @Produces
  @Singleton
  public Template produce() {
    try {
      Engine templateEngine = Engine.builder().addDefaults().build();
      // read all bytes
      byte[] bytes = Files.readAllBytes(Paths.get(config.templatePath()));
      // convert bytes to string
      String content = new String(bytes, Charset.forName(config.charset()));

      Template template = templateEngine.parse(content);
      return template;
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

}
