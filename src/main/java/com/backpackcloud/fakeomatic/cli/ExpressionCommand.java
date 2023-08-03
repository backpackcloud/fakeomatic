package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.cli.Action;
import com.backpackcloud.cli.AnnotatedCommand;
import com.backpackcloud.cli.CommandDefinition;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.Suggestions;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandDefinition(
  name = "expression",
  description = "Generates a sample data based on an expression",
  type = "Sample Generation"
)
@RegisterForReflection
@ApplicationScoped
public class ExpressionCommand implements AnnotatedCommand {

  private final Sampler sampler;

  public ExpressionCommand(Sampler sampler) {
    this.sampler = sampler;
  }

  @Action
  public String execute(String expression) {
    return sampler.expression(expression);
  }

  @Suggestions
  public List<Suggestion> suggest(CommandInput input) {
    if (input.isEmpty()) {
      return sampler.placeholders().entrySet().stream()
        .map(entry -> PromptSuggestion.suggest(entry.getKey().toString())
          .describedAs(entry.getValue()))
        .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

}
