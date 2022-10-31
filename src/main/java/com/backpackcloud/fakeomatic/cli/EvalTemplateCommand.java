package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.cli.Action;
import com.backpackcloud.cli.AnnotatedCommand;
import com.backpackcloud.cli.CommandDefinition;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import com.backpackcloud.fakeomatic.sampler.TemplateEval;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.enterprise.context.ApplicationScoped;

@CommandDefinition(
  name = "eval",
  description = "Evaluates the given template",
  type = "Sample Generation"
)
@RegisterForReflection
@ApplicationScoped
public class EvalTemplateCommand implements AnnotatedCommand {

  private final TemplateEval templateEval;

  public EvalTemplateCommand(Sampler sampler) {
    this.templateEval = new TemplateEval(sampler);
  }

  @Action
  public String execute(String template) {
    return templateEval.eval(template);
  }

}
