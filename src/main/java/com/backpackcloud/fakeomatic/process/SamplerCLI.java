package com.backpackcloud.fakeomatic.process;

import com.backpackcloud.cli.CLI;
import com.backpackcloud.fakeomatic.cli.EvalTemplateCommand;
import com.backpackcloud.fakeomatic.cli.ExpressionCommand;
import com.backpackcloud.fakeomatic.cli.SamplerCommand;
import com.backpackcloud.sampler.Sampler;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SamplerCLI implements QuarkusApplication {

  private final Sampler sampler;

  public SamplerCLI(Sampler sampler) {
    this.sampler = sampler;
  }

  @Override
  public int run(String... args) {
    CLI cli = CLI.complete()
      .configureFactory(factory -> factory.context()
        .whenType(Sampler.class)
        .use(sampler))

      .addCommand(SamplerCommand.class)
      .addCommand(ExpressionCommand.class)
      .addCommand(EvalTemplateCommand.class)

      .build();

    cli.start();

    return 0;
  }

}
