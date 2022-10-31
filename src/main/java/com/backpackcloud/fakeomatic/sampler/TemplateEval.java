package com.backpackcloud.fakeomatic.sampler;

import com.backpackcloud.text.Interpolator;

import java.util.regex.Pattern;

public class TemplateEval {

  private final Interpolator samplesInterpolator;
  private final Interpolator expressionsInterpolator;

  public TemplateEval(Sampler sampler) {
    this.samplesInterpolator = new Interpolator(
      Pattern.compile("\\{\\{(?<token>[^}]+)\\}\\}"),
      sampler::some
    );
    this.expressionsInterpolator = new Interpolator(
      Pattern.compile("\\[\\[(?<token>[^]]+)\\]\\]"),
      sampler::expression
    );
  }

  public String eval(String template) {
    String result = expressionsInterpolator.eval(template).orElse("");
    return samplesInterpolator.eval(result).orElse("");
  }

}
