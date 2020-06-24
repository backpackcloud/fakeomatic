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

package io.backpackcloud.fakeomatic.command;

import io.backpackcloud.fakeomatic.process.Generator;
import io.quarkus.runtime.Quarkus;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class GeneratorCommand implements Callable<Integer> {

  private static final Logger LOGGER = Logger.getLogger(GeneratorCommand.class);

  @CommandLine.Option(
      names = {"-e", "--endpoint"},
      description = "The endpoint url"
  )
  String endpointUrl;

  @CommandLine.Option(
      names = {"-c", "--concurrency"},
      description = "The maximum number of ongoing requests to the endpoint"
  )
  String concurrency;

  @CommandLine.Option(
      names = {"-i", "--insecure"},
      description = "Trusts all certificates for the endpoint connection"
  )
  String insecure;

  @CommandLine.Option(
      names = {"-t", "--total"},
      description = "The total number of payloads to create"
  )
  String total;

  @CommandLine.Option(
      names = {"-b", "--buffer"},
      description = "How many payloads should be kept on a buffer while waiting for ongoing requests"
  )
  String buffer;

  @CommandLine.Option(
      names = {"-f", "--configs"},
      description = "The configurations to apply"
  )
  String configs;

  @CommandLine.Option(
      names = {"-s", "--seed"},
      description = "The seed to use for randomness"
  )
  String seed;

  @CommandLine.Option(
      names = {"-p", "--template"},
      description = "The template to use for payload generation"
  )
  String templatePath;

  @CommandLine.Option(
      names = {"-a", "--template-type"},
      description = "Defines the content type that the template produces"
  )
  String templateType;

  @CommandLine.Option(
      names = "--events-log-level",
      description = "Log level for the events"
  )
  String eventsLogLevel;

  @Override
  public Integer call() {

    setPropertyIfNotNull("endpoint.url", endpointUrl);
    setPropertyIfNotNull("endpoint.concurrency", concurrency);
    setPropertyIfNotNull("endpoint.insecure", insecure);

    setPropertyIfNotNull("generator.total", total);
    setPropertyIfNotNull("generator.buffer", buffer);
    setPropertyIfNotNull("generator.configs", configs);
    setPropertyIfNotNull("generator.seed", seed);

    setPropertyIfNotNull("template.path", templatePath);
    setPropertyIfNotNull("template.type", templateType);

    setPropertyIfNotNull("events.log.level", eventsLogLevel);

    try {
      Quarkus.run(Generator.class);
    } catch (Throwable e) {
      LOGGER.error("Error while faking data", e);
      return 1;
    }
    return 0;
  }

  private void setPropertyIfNotNull(String key, String value) {
    if (value != null) {
      System.setProperty(key, value);
    }
  }

}
