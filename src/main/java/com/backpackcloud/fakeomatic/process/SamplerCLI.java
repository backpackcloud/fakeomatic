package com.backpackcloud.fakeomatic.process;

import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.Registry;
import com.backpackcloud.cli.ResourceCollection;
import com.backpackcloud.cli.impl.ResourceCLI;
import com.backpackcloud.fakeomatic.cli.EvalTemplateCommand;
import com.backpackcloud.fakeomatic.cli.ExpressionCommand;
import com.backpackcloud.fakeomatic.cli.SamplerCommand;
import com.backpackcloud.sampler.Sampler;
import io.quarkus.runtime.QuarkusApplication;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class SamplerCLI implements QuarkusApplication {

  private final Sampler sampler;

  public SamplerCLI(Sampler sampler) {
    this.sampler = sampler;
  }

  @Override
  public int run(String... args) {
    Registry registry = Registry.defaults();
    Collection<Command> commands = registry.commands();
    ResourceCollection resources = registry.resources();

    commands.add(new SamplerCommand(sampler, resources));
    commands.add(new ExpressionCommand(sampler, resources));
    commands.add(new EvalTemplateCommand(sampler, resources));

    CLI cli = new ResourceCLI(registry);
    cli.start();

    return 0;
  }

}
