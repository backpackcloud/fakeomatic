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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.UnbelievableException;
import io.backpackcloud.fakeomatic.spi.Configuration;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RegisterForReflection
public class FileContentConfiguration implements Configuration {

  private final String path;

  // TODO add support for defining the file location using environment variables or system properties
  @JsonCreator
  public FileContentConfiguration(@JsonProperty("path") String path) {
    this.path = path;
  }

  @Override
  public boolean isSet() {
    return new File(path).isFile();
  }

  @Override
  public String get() {
    try {
      return Files.readString(Path.of(path));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

}
