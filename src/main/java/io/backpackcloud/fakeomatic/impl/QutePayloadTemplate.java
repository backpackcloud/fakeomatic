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

package io.backpackcloud.fakeomatic.impl;

import io.backpackcloud.fakeomatic.spi.Configuration;
import io.backpackcloud.fakeomatic.spi.Faker;
import io.backpackcloud.fakeomatic.spi.Payload;
import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class QutePayloadTemplate implements Payload {

  private final String           contentType;
  private final TemplateInstance templateInstance;

  public QutePayloadTemplate(String contentType, TemplateInstance templateInstance) {
    this.contentType = contentType;
    this.templateInstance = templateInstance;
  }

  @Override
  public String contentType() {
    return contentType;
  }

  @Override
  public String content() {
    return templateInstance.render();
  }

  public static Payload newInstance(Engine engine, Faker faker, Configuration template, Configuration contentType) {
    String           content          = template.read();
    TemplateInstance templateInstance = engine.parse(content).data(faker);

    return new QutePayloadTemplate(contentType.get(), templateInstance);
  }

}
