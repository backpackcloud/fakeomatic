package io.backpackcloud.fakeomatic.spi.samples;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RegisterForReflection
public class WeightedSample implements Sample {

  private final List<WeightedValue> values;

  private final int totalWeight;

  @JsonCreator
  public WeightedSample(@JsonProperty("values") List<WeightedValueDefinition> definitions) {
    this.values = new ArrayList<>(definitions.size());
    int current = 0;
    for (WeightedValueDefinition definition : definitions) {
      this.values.add(new WeightedValue(current, definition));
      current += definition.weight();
    }
    totalWeight = current;
  }

  public int totalWeight() {
    return totalWeight;
  }

  @Override
  public String get(Random random) {
    int position = random.nextInt(totalWeight);
    return values.stream()
                 .filter(weightedValue -> weightedValue.isSelected(position))
                 .map(WeightedValue::value)
                 .findFirst()
                 .get();
  }

  @RegisterForReflection
  public static class WeightedValueDefinition {

    private final int weight;

    private final String value;

    @JsonCreator
    public WeightedValueDefinition(@JsonProperty("weight") int weight, @JsonProperty("value") String value) {
      this.weight = weight;
      this.value = value;
    }

    public int weight() {
      return weight;
    }

    public String value() {
      return value;
    }

  }

  class WeightedValue {

    private final String value;
    private final int    minPosition;
    private final int    maxPosition;

    WeightedValue(int currentPosition, WeightedValueDefinition definition) {
      this.value = definition.value();
      this.minPosition = currentPosition;
      this.maxPosition = currentPosition + definition.weight();
    }

    public boolean isSelected(int position) {
      return position >= minPosition && position < maxPosition;
    }

    public String value() {
      return value;
    }

  }

}
