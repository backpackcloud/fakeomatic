package com.backpackcloud.fakeomatic.process;

import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.Main;
import com.backpackcloud.fakeomatic.cli.EvalTemplateCommand;
import com.backpackcloud.fakeomatic.cli.ExpressionCommand;
import com.backpackcloud.fakeomatic.cli.SamplerCommand;
import com.backpackcloud.sampler.Sampler;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.TreeSet;

@ApplicationScoped
public class SamplerCLI implements QuarkusApplication {

  private final Sampler sampler;

  public SamplerCLI(Sampler sampler) {
    this.sampler = sampler;
  }

  @Override
  public int run(String... args) {
    CLI cli = CLI.complete()
      .addDependency(Sampler.class, sampler)

      .addCommands(Arrays.asList(
        SamplerCommand.class,
        ExpressionCommand.class,
        EvalTemplateCommand.class))

      .build();

    //cli.start();

    CLI.complete()
      .resourceCollection(new TreeSet<>())
      .addCommand(Main.PushCommand.class)
      .build()
      .start();

    return 0;
  }

}
