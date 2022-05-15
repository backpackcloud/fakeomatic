package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandType;
import com.backpackcloud.cli.commands.GeneralCommandType;
import com.backpackcloud.fakeomatic.sampler.Sampler;

import javax.enterprise.context.ApplicationScoped;
import java.util.function.Function;

@ApplicationScoped
public class EvalTemplateCommand implements Command {

  private final Function<String, String> interpolator;

  public EvalTemplateCommand(Sampler sampler) {
    this.interpolator = sampler.interpolator();
  }

  @Override
  public String name() {
    return "eval";
  }

  @Override
  public CommandType type() {
    return GeneralCommandType.DATA;
  }

  @Override
  public String description() {
    return "Evaluates the given template";
  }

  @Override
  public void execute(CommandContext context) {
    String template = context.input().asString();
    context.writer().write(interpolator.apply(template)).newLine();
  }

}
