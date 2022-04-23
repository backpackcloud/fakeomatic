package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.Label;
import com.backpackcloud.Tag;
import com.backpackcloud.UnbelievableException;
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
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SamplerCommand implements Command {

  private final Sampler sampler;
  private final ResourceCollection collection;

  public SamplerCommand(Sampler sampler, ResourceCollection collection) {
    this.sampler = sampler;
    this.collection = collection;
  }

  @Override
  public String name() {
    return "sample";
  }

  @Override
  public CommandType type() {
    return GeneralCommandType.DATA;
  }

  @Override
  public String description() {
    return "Generates a sample data";
  }

  @Override
  public void execute(CommandContext context) {
    String sampleName = context.input().asString();
    Sample sample = sampler.sample(sampleName)
      .orElseThrow(UnbelievableException.because("Sample not found"));

    SimpleResource resource = new SimpleResource(sample.get().toString());

    resource.labels().put(new Label("type", sample.type()));
    resource.labels().put(new Label("sample", sampleName));
    resource.tags().put(new Tag("sample"));

    collection.add(resource);

    Writer writer = context.writer();
    resource.toDisplay(writer);
    writer.newLine();
  }

  @Override
  public List<Suggestion> suggest(CommandInput input) {
    if (input.asList().size() > 1) {
      return Collections.emptyList();
    }
    return sampler.samples().entrySet().stream()
      .map(entry -> PromptSuggestion.suggest(entry.getKey())
        .describedAs(entry.getValue().type()))
      .collect(Collectors.toList());
  }

}
