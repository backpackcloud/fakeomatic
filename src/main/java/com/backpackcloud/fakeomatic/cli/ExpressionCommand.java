package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.Label;
import com.backpackcloud.Tag;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.CommandType;
import com.backpackcloud.cli.ResourceCollection;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.commands.GeneralCommandType;
import com.backpackcloud.cli.impl.SimpleResource;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import com.backpackcloud.sampler.Sampler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExpressionCommand implements Command {

  private final Sampler sampler;
  private final ResourceCollection collection;

  public ExpressionCommand(Sampler sampler, ResourceCollection collection) {
    this.sampler = sampler;
    this.collection = collection;
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
    String value = sampler.expression(expression);

    SimpleResource resource = new SimpleResource(value,
      (simpleResource, writer) -> writer
        .write(String.format("[%s] ", expression), "blue")
        .write(simpleResource.value(), "white")
    );

    resource.labels().put(new Label("expression", expression));
    resource.tags().put(new Tag("expression"));

    collection.add(resource);

    Writer writer = context.writer();
    resource.toDisplay(writer);
    writer.newLine();
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
