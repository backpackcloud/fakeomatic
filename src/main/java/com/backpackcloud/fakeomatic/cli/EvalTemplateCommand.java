package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.Tag;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandType;
import com.backpackcloud.cli.ResourceCollection;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.commands.GeneralCommandType;
import com.backpackcloud.cli.impl.SimpleResource;
import com.backpackcloud.sampler.Sampler;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.function.Function;

@RegisterForReflection
public class EvalTemplateCommand implements Command {

  private final Function<String, String> interpolator;
  private final ResourceCollection collection;

  public EvalTemplateCommand(Sampler sampler, ResourceCollection collection) {
    this.interpolator = sampler.interpolator();
    this.collection = collection;
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
    String value = interpolator.apply(template);

    SimpleResource resource = new SimpleResource(value);

    resource.tags().put(new Tag("template"));

    collection.add(resource);

    Writer writer = context.writer();
    resource.toDisplay(writer);
    writer.newLine();
  }

}
