package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.CommandType;
import com.backpackcloud.cli.Writer;
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

  public SamplerCommand(Sampler sampler) {
    this.sampler = sampler;
  }

  @Override
  public String name() {
    return "sample";
  }

  @Override
  public CommandType type() {
    return FakeomaticCommandType.SAMPLE;
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

    Writer writer = context.writer();
    writer.write(sample.get().toString());
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
