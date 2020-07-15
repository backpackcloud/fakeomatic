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

import io.backpackcloud.fakeomatic.core.spi.Faker;
import io.backpackcloud.fakeomatic.spi.Endpoint;
import io.backpackcloud.fakeomatic.spi.FakeOMatic;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;
import java.util.Optional;

@RegisterForReflection
public class FakeOMaticImpl implements FakeOMatic {

  private final Map<String, Endpoint> endpoints;
  private final Faker faker;

  public FakeOMaticImpl(Map<String, Endpoint> endpoints, Faker faker) {
    this.endpoints = endpoints;
    this.faker = faker;
  }

  @Override
  public Optional<Endpoint> endpoint(String name) {
    return Optional.ofNullable(endpoints.get(name));
  }

  @Override
  public Faker faker() {
    return faker;
  }

}
