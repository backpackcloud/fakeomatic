package com.backpackcloud.fakeomatic.process;

import com.backpackcloud.cli.CLI;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SamplerCLI implements QuarkusApplication {

  private final CLI cli;

  public SamplerCLI(CLI cli) {
    this.cli = cli;
  }

  @Override
  public int run(String... args) {
    cli.start();
    return 0;
  }

}
