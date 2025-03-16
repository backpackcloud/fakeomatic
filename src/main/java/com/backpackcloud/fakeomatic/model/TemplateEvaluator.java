package com.backpackcloud.fakeomatic.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEvaluator {

  private static final Pattern SAMPLES_PATTERN = Pattern.compile("\\{\\{(?<sample>[^}]+)}}");
  private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\[\\[(?<expression>[^]]+)]]");
  private final Sampler sampler;

  public TemplateEvaluator(Sampler sampler) {
    this.sampler = sampler;
  }

  public String eval(String template) {
    Matcher matcher = SAMPLES_PATTERN.matcher(template);
    String result = matcher.replaceAll(match -> sampler.some(match.group("sample")));

    matcher = EXPRESSION_PATTERN.matcher(result);
    result = matcher.replaceAll(match -> sampler.expression(match.group("expression")));

    return result;
  }

}
