package com.backpackcloud.fakeomatic.model.samples;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.fakeomatic.model.Sample;
import com.backpackcloud.fakeomatic.model.Sampler;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.Supplier;

public class ExpressionSample implements Sample<String> {

  public static final String TYPE = "expression";

  private final Sampler sampler;
  private final Supplier<?> expressionSupplier;

  public ExpressionSample(Supplier<?> expressionSupplier, Sampler sampler) {
    this.expressionSupplier = expressionSupplier;
    this.sampler = sampler;
  }

  @Override
  public String type() {
    return TYPE;
  }

  public String get() {
    String expression = this.expressionSupplier.get().toString();
    return this.sampler.expression(expression);
  }

  @JsonCreator
  public static ExpressionSample create(@JsonProperty("sample") String sampleName,
                                        @JsonProperty("expression") String expression,
                                        @JacksonInject Sampler sampler) {
    if (sampleName != null) {
      return new ExpressionSample(sampler.sample(sampleName), sampler);
    } else if (expression != null) {
      return new ExpressionSample(() -> expression, sampler);
    } else {
      throw new UnbelievableException("No sample or expression given.");
    }
  }
}
