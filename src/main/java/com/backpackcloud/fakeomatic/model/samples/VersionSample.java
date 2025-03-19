package com.backpackcloud.fakeomatic.model.samples;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.fakeomatic.model.Sample;
import com.backpackcloud.text.InputValue;
import com.backpackcloud.versiontm.Precision;
import com.backpackcloud.versiontm.Version;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.random.RandomGenerator;

public class VersionSample implements Sample<Version> {

  public static final String TYPE = "version";

  private final RandomGenerator random;
  private final Precision precision;
  private final SegmentRange majorSegmentRange;
  private final SegmentRange minorSegmentRange;
  private final SegmentRange microSegmentRange;

  public VersionSample(RandomGenerator random,
                       Precision precision,
                       SegmentRange majorSegmentRange,
                       SegmentRange minorSegmentRange,
                       SegmentRange microSegmentRange) {
    this.random = random;
    this.precision = precision;
    this.majorSegmentRange = majorSegmentRange;
    this.minorSegmentRange = minorSegmentRange;
    this.microSegmentRange = microSegmentRange;
  }

  @Override
  public Version get() {
    return switch (precision) {
      case NONE -> Version.NULL;
      case MAJOR -> new Version(random.nextInt(majorSegmentRange.min(), majorSegmentRange.max()));
      case MINOR -> new Version(
        random.nextInt(majorSegmentRange.min(), majorSegmentRange.max()),
        random.nextInt(minorSegmentRange.min(), minorSegmentRange.max())
      );
      case MICRO -> new Version(
        random.nextInt(majorSegmentRange.min(), majorSegmentRange.max()),
        random.nextInt(minorSegmentRange.min(), minorSegmentRange.max()),
        random.nextInt(microSegmentRange.min(), microSegmentRange.max())
      );
    };
  }

  @Override
  public String type() {
    return TYPE;
  }

  @JsonCreator
  public static VersionSample create(@JacksonInject RandomGenerator random,
                                     @JsonProperty("precision") InputValue precisionInput,
                                     @JsonProperty("major") SegmentRange majorSegmentRange,
                                     @JsonProperty("minor") SegmentRange minorSegmentRange,
                                     @JsonProperty("micro") SegmentRange microSegmentRange) {
    Precision precision = precisionInput.asEnum(Precision.class)
      .orElseThrow(UnbelievableException.because("Invalid precision"));

    return new VersionSample(random, precision, majorSegmentRange, minorSegmentRange, microSegmentRange);
  }

  public record SegmentRange(int min, int max) {

    @JsonCreator
    public static SegmentRange create(@JsonProperty("min") InputValue minInput,
                                      @JsonProperty("max") InputValue maxInput) {
      int min, max;

      if (minInput != null) {
        min = Math.max(minInput.asInteger().orElse(Version.MIN_SEGMENT_VALUE), Version.MIN_SEGMENT_VALUE);
      } else {
        min = Version.MIN_SEGMENT_VALUE;
      }

      if (maxInput != null) {
        max = Math.min(maxInput.asInteger().orElse(Version.MAX_SEGMENT_VALUE), Version.MAX_SEGMENT_VALUE);
      } else {
        max = Version.MAX_SEGMENT_VALUE;
      }

      return new SegmentRange(min, max);
    }

  }

}
