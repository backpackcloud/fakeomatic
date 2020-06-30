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

package io.backpackcloud.fakeomatic.impl.configuration;

import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Configuration;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UrlConfiguration implements Configuration {

  private final URL    url;
  private       String content;

  public UrlConfiguration(String url) {
    try {
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      throw new UnbelievableException(e);
    }
  }

  @Override
  public boolean isSet() {
    // TODO check if the link is up
    return true;
  }

  @Override
  public String get() {
    return url.toExternalForm();
  }

  @Override
  public String read() {
    load();
    return content;
  }

  @Override
  public List<String> readLines() {
    load();
    return content.lines().collect(Collectors.toList());
  }

  private void load() {
    if (content != null) {
      HttpResponse response = WebClient.create(Vertx.vertx(), new WebClientOptions()
          .setDefaultHost(this.url.getHost())
          .setDefaultPort(this.url.getPort() == -1 ? this.url.getDefaultPort() : this.url.getPort())
          .setSsl("https".equals(this.url.getProtocol())))
                                       .get(url.toString())
                                       .send()
                                       .onItem()
                                       .apply(Function.identity())
                                       // TODO externalize this
                                       .await().atMost(Duration.ofSeconds(30));
      content = response.bodyAsString();
    }
  }

}
