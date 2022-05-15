package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.CommandType;
import com.backpackcloud.cli.commands.GeneralCommandType;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import com.backpackcloud.fakeomatic.sampler.Sampler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExpressionCommand implements Command {

  private final Sampler sampler;

  public ExpressionCommand(Sampler sampler) {
    this.sampler = sampler;
  }

  @Override
  public String name() {
    return "expression";
  }

  @Override
  public CommandType type() {
    return GeneralCommandType.DATA;
  }

  @Override
  public String description() {
    return "Generates a sample data based on an expression";
  }

  @Override
  public void execute(CommandContext context) {
    String expression = context.input().asString();
    context.writer().write(sampler.expression(expression)).newLine();
  }

  @Override
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
