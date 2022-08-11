package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandType;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.fakeomatic.sampler.TemplateEval;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EvalTemplateCommand implements Command {

  private final TemplateEval templateEval;

  public EvalTemplateCommand(Sampler sampler) {
    this.templateEval = new TemplateEval(sampler);
  }

  @Override
  public String name() {
    return "eval";
  }

  @Override
  public CommandType type() {
    return FakeomaticCommandType.SAMPLE;
  }

  @Override
  public String description() {
    return "Evaluates the given template";
  }

  @Override
  public void execute(CommandContext context) {
    String template = context.input().asString();

    context.writer().write(templateEval.eval(template)).newLine();
  }

}
