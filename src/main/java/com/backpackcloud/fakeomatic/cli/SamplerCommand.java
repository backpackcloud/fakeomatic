package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.Action;
import com.backpackcloud.cli.AnnotatedCommand;
import com.backpackcloud.cli.CommandDefinition;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.Suggestions;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import com.backpackcloud.fakeomatic.sampler.Sample;
import com.backpackcloud.fakeomatic.sampler.Sampler;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandDefinition(
  name = "sample",
  description = "Generates a sample data",
  type = "Sample Generation"
)
@RegisterForReflection
@ApplicationScoped
public class SamplerCommand implements AnnotatedCommand {

  private final Sampler sampler;

  public SamplerCommand(Sampler sampler) {
    this.sampler = sampler;
  }

  @Action
  public String execute(String sampleName) {
    Sample sample = sampler.sample(sampleName)
      .orElseThrow(UnbelievableException.because("Sample not found"));

    return sample.get().toString();
  }

  @Suggestions
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
