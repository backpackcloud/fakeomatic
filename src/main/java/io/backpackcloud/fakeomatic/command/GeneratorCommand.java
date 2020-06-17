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
  String trustAll;

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
      names = {"-d", "--template-encode"},
      description = "Which encode to use for parsing the template file"
  )
  String templateEncode;

  @Override
  public Integer call() {

    setPropertyIfNotNull("endpoint.url", endpointUrl);
    setPropertyIfNotNull("endpoint.concurrency", concurrency);
    setPropertyIfNotNull("endpoint.trustAll", trustAll);

    setPropertyIfNotNull("generator.total", total);
    setPropertyIfNotNull("generator.buffer", buffer);
    setPropertyIfNotNull("generator.configs", configs);
    setPropertyIfNotNull("generator.seed", seed);

    setPropertyIfNotNull("template.path", templatePath);
    setPropertyIfNotNull("template.type", templateType);
    setPropertyIfNotNull("template.charset", templateEncode);

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
