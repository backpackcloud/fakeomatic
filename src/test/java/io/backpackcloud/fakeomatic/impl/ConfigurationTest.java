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
import io.backpackcloud.fakeomatic.BaseTest;
import io.backpackcloud.fakeomatic.spi.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationTest extends BaseTest {

  TestObject testObject;

  @BeforeEach
  public void init() throws Exception {
    testObject = objectMapper.readValue(
        new FileInputStream(
            new File("src/test/resources/io/backpackcloud/fakeomatic/impl/values.yaml")
        ),
        TestObject.class
    );
    System.clearProperty("fakeomatic.test");
  }

  private Configuration value(String key) {
    return testObject.map.get(key);
  }

  @Test
  public void testRawValue() {
    Configuration value = value("raw_value");
    assertTrue(value.isSet());
    assertEquals("foo", value.get());
  }

  @Test
  public void testEnvironmentVariableValue() {
    Configuration value = value("env_value");
    assertTrue(value.isSet());
    assertFalse(value.get().isEmpty());
  }

  @Test
  public void testSystemPropertyValue() {
    Configuration value = value("property_value");
    assertFalse(value.isSet());
    System.setProperty("fakeomatic.test", "bar");
    assertTrue(value.isSet());
    assertEquals("bar", value.get());
  }

  @Test
  public void testFileValue() {
    Configuration value = value("file_value");
    assertTrue(value.isSet());
    assertTrue(value.get().contains("MIT"));
  }

  @Test
  public void testResourceValue() {
    Configuration value = value("resource_value");
    assertTrue(value.isSet());
    assertTrue(value.get().contains("placeholders:"));
  }

  public static class TestObject {

    public final Map<String, Configuration> map;

    @JsonCreator
    public TestObject(@JsonProperty("map") Map<String, Configuration> map) {
      this.map = map;
    }

  }

}
