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

package com.backpackcloud.fakeomatic.command;

import com.backpackcloud.fakeomatic.process.Api;
import com.backpackcloud.fakeomatic.process.Generator;
import com.backpackcloud.fakeomatic.process.SamplerCLI;
import io.quarkus.runtime.Quarkus;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class GeneratorCommand implements Callable<Integer> {

  @CommandLine.Option(
    names = {"-f", "--config"},
    description = "A configuration to use"
  )
  String config;

  @CommandLine.Option(
    names = {"-r", "--seed"},
    description = "The seed to use for randomness"
  )
  String seed;

  @CommandLine.Option(
    names = {"-t", "--template"},
    description = "Sets the operation to be a template parse"
  )
  boolean template;

  @CommandLine.Option(
    names = {"-e", "--expression"},
    description = "Sets the operation to be an expression parse"
  )
  boolean expression;

  @CommandLine.Parameters(
    arity = "0..1",
    description = "The sample to generate"
  )
  String value;

  @CommandLine.Option(
    names = {"-s", "--server"},
    description = "Starts the server instead of generating data"
  )
  boolean serverMode;

  @CommandLine.Option(
    names = {"-i", "--cli"},
    description = "Starts the command line interface"
  )
  boolean cli;

  @CommandLine.Option(
    names = {"-b", "--host"},
    description = "Sets the host for the server"
  )
  String host;

  @CommandLine.Option(
    names = {"-p", "--port"},
    description = "Sets the port for the server"
  )
  String port;

  @Override
  public Integer call() {

    setPropertyIfNotNull("generator.seed", seed);
    if (config != null) {
      setPropertyIfNotNull("generator.config", config);
    }

    if (serverMode) {
      setPropertyIfNotNull("quarkus.http.host", host);
      setPropertyIfNotNull("quarkus.http.port", port);
      System.setProperty("quarkus.log.level", "INFO");

      try {
        Quarkus.run(Api.class);
      } catch (Exception e) {
        e.printStackTrace();
        return 1;
      }

    } else if (cli) {
      System.setProperty("quarkus.log.level", "ERROR");
      System.setProperty("quarkus.log.console.enable", "true");
      System.setProperty("quarkus.banner.enabled", "false");
      System.setProperty("quarkus.console.disable-input", "true");
      System.setProperty("quarkus.test.continuous-testing", "disabled");

      try {
        Quarkus.run(SamplerCLI.class);
      } catch (Exception e) {
        e.printStackTrace();
        return 1;
      }
    } else {
      String mode = template ? "template" : expression ? "expression" : "sample";

      System.setProperty("quarkus.log.level", "ERROR");
      System.setProperty("quarkus.log.console.enable", "true");
      System.setProperty("quarkus.banner.enabled", "false");
      System.setProperty("quarkus.console.disable-input", "true");
      System.setProperty("quarkus.test.continuous-testing", "disabled");

      try {
        Quarkus.run(Generator.class, mode, value);
      } catch (Exception e) {
        e.printStackTrace();
        return 1;
      }
    }

    return 0;
  }

  private void setPropertyIfNotNull(String key, String value) {
    if (value != null) {
      System.setProperty(key, value);
    }
  }

}
