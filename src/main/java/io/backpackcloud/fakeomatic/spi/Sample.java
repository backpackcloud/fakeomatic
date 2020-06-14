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

package io.backpackcloud.fakeomatic.spi;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.backpackcloud.fakeomatic.spi.samples.ApiSample;
import io.backpackcloud.fakeomatic.spi.samples.CompositeSample;
import io.backpackcloud.fakeomatic.spi.samples.ListSample;
import io.backpackcloud.fakeomatic.spi.samples.CharSample;
import io.backpackcloud.fakeomatic.spi.samples.UuidSample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a collection of data that can be randomized.
 *
 * @author Marcelo Guimarães
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ApiSample.class,       name = "api"),
    @JsonSubTypes.Type(value = CharSample.class,      name = "chars"),
    @JsonSubTypes.Type(value = CompositeSample.class, name = "composite"),
    @JsonSubTypes.Type(value = ListSample.class,      name = "list"),
    @JsonSubTypes.Type(value = UuidSample.class,      name = "uuid"),
})
@FunctionalInterface
public interface Sample {

  /**
   * Returns a random data using the given Random object for picking the data.
   *
   * @param random the random object to use for randomness.
   * @return a random data.
   */
  String get(Random random);

  /**
   * Returns a list of random data. The list might contain duplicated entries.
   *
   * @param size   the size of the list.
   * @param random the random object to use for randomness.
   * @return a new list with randomized data.
   */
  default List<String> get(int size, Random random) {
    List<String> result = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      result.add(get(random));
    }
    return result;
  }

}
