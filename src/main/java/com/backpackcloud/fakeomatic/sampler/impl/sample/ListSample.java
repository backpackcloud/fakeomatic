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

package com.backpackcloud.fakeomatic.sampler.impl.sample;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.serializer.Serializer;
import com.backpackcloud.text.InputValue;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.CSVReader;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * This sample can pick any item from a given list of objects. The object will be used in its
 * `string` form. Useful for defining a set of data that is meant to be read, like cities and names.
 *
 * @author Marcelo Guimarães
 */
@RegisterForReflection
public class ListSample<E> implements Sample<E> {

  public static final String TYPE = "list";

  private final RandomGenerator random;
  private final List<Sample<E>> samples;

  public ListSample(RandomGenerator random, List<Sample<E>> samples) {
    if (samples.isEmpty()) {
      throw new UnbelievableException("Empty sample list");
    }
    this.random = random;
    this.samples = samples;
  }

  @Override
  public String type() {
    return TYPE;
  }

  public List<Sample<E>> samples() {
    return samples;
  }

  @Override
  public E get() {
    int index = random.nextInt(samples.size());
    Sample<E> randomSample = samples.get(index);
    return randomSample.get();
  }

  @JsonCreator
  public static ListSample<?> create(@JacksonInject RandomGenerator random,
                                     @JacksonInject Sampler sampler,
                                     @JacksonInject Serializer serializer,
                                     @JsonProperty("values") List<Object> values,
                                     @JsonProperty("samples") List<String> samplesNames,
                                     @JsonProperty("source") Configuration source,
                                     @JsonProperty("json") Configuration jsonSource,
                                     @JsonProperty("yaml") Configuration yamlSource,
                                     @JsonProperty("column") InputValue columnInput,
                                     @JsonProperty("offset") InputValue offsetInput,
                                     @JsonProperty("csv") Configuration csv) {
    List<Sample> samples;
    if (values != null) {
      samples = values.stream()
        .map(Sample::of)
        .collect(Collectors.toList());
    } else if (samplesNames != null) {
      samples = samplesNames.stream()
        .map(sampler::sample)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    } else if (source.isSet()) {
      samples = source.readLines().stream()
        .map(Sample::of)
        .collect(Collectors.toList());
    } else if (jsonSource.isSet()) {
      samples = serializer.deserialize(jsonSource.read(), List.class).stream()
        .map(Sample::of)
        .toList();
    } else if (yamlSource.isSet()) {
      samples = serializer.deserialize(yamlSource.read(), List.class).stream()
        .map(Sample::of)
        .toList();
    } else if (csv.isSet()) {
      CSVReader csvReader = new CSVReader(new StringReader(csv.read()));

      try {
        List<String[]> all = csvReader.readAll();

        int column;
        int offset;

        if (columnInput.integer().isEmpty() && columnInput.text().isPresent()) {
          String columnName = columnInput.text().get();
          List<String> firstRow = List.of(all.getFirst());

          column = firstRow.indexOf(columnName);
          offset = offsetInput.integer().orElse(1);

          if (column == -1) {
            throw new UnbelievableException("Can't find column " + columnName + " in CSV Header: " + firstRow);
          }
        } else {
          column = columnInput.integer().orElse(0);
          offset = offsetInput.integer().orElse(0);
        }

        samples = all.subList(0, offset).stream()
          .map(row -> row[column])
          .map(Sample::of)
          .collect(Collectors.toList());
      } catch (Exception e) {
        throw new UnbelievableException(e);
      }
    } else {
      throw new UnbelievableException("No valid configuration supplied");
    }
    return new ListSample(random, samples);
  }

}
