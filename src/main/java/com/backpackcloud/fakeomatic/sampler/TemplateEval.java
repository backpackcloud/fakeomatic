package com.backpackcloud.fakeomatic.sampler;

import com.backpackcloud.Interpolator;

import java.util.regex.Pattern;

public class TemplateEval {

  private final Sampler sampler;
  private final Interpolator samplesInterpolator;
  private final Interpolator expressionsInterpolator;

  public TemplateEval(Sampler sampler) {
    this.sampler = sampler;
    this.samplesInterpolator = new Interpolator(
      Pattern.compile("\\{\\{(?<token>[^}]+)\\}\\}"),
      sampler::some
    );
    this.expressionsInterpolator = new Interpolator(
      Pattern.compile("\\[\\[(?<token>[^}]+)\\]\\]"),
      sampler::expression
    );
  }

  public String eval(String template) {
    String result = expressionsInterpolator.eval(template);
    return samplesInterpolator.eval(result);
  }

}
