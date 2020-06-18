package io.backpackcloud.fakeomatic.spi.sample;

import io.backpackcloud.fakeomatic.BaseTest;
import io.backpackcloud.fakeomatic.spi.samples.WeightedSample;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeightedSampleTest extends BaseTest {

  @Test
  public void test() {
    testSample(
        new WeightedSample.WeightedValueDefinition(50, "foo"),
        new WeightedSample.WeightedValueDefinition(50, "bar")
    );

    testSample(
        new WeightedSample.WeightedValueDefinition(20, "foo"),
        new WeightedSample.WeightedValueDefinition(80, "bar")
    );

    testSample(
        new WeightedSample.WeightedValueDefinition(15, "foo"),
        new WeightedSample.WeightedValueDefinition(60, "bar"),
        new WeightedSample.WeightedValueDefinition(25, "meh")
    );

    testSample(
        new WeightedSample.WeightedValueDefinition(15, "foo"),
        new WeightedSample.WeightedValueDefinition(60, "bar"),
        new WeightedSample.WeightedValueDefinition(25, "meh"),
        new WeightedSample.WeightedValueDefinition(0, "baz")
    );

    testSample(
        new WeightedSample.WeightedValueDefinition(15, "foo"),
        new WeightedSample.WeightedValueDefinition(60, "bar"),
        new WeightedSample.WeightedValueDefinition(24, "meh"),
        new WeightedSample.WeightedValueDefinition(1, "baz")
    );

    testSample(
        new WeightedSample.WeightedValueDefinition(25, "foo"),
        new WeightedSample.WeightedValueDefinition(40, "bar"),
        new WeightedSample.WeightedValueDefinition(35, "meh"),
        new WeightedSample.WeightedValueDefinition(55, "baz")
    );
  }

  private void testSample(WeightedSample.WeightedValueDefinition... definitions) {
    WeightedSample       sample      = new WeightedSample(Arrays.asList(definitions));
    Map<String, Integer> occurrences = new HashMap<>();
    Random               random      = new Random();
    int                  total       = 1000000;
    int                  errorMargin = 1;

    Arrays.stream(definitions).forEach(def -> occurrences.put(def.value(), 0));

    times(total, () -> occurrences.compute(sample.get(random), (s, integer) -> integer + 1));

    assertEquals(total, (Integer) occurrences.values().stream().mapToInt(Integer::intValue).sum());

    assertTrue(
        Arrays.stream(definitions)
              .map(def -> def.weight() - occurrences.get(def.value()) * sample.totalWeight() / total)
              .allMatch(distance -> distance <= errorMargin)
    );
  }

}
