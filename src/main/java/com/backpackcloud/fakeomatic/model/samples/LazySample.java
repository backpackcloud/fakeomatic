package com.backpackcloud.fakeomatic.model.samples;

import com.backpackcloud.fakeomatic.model.Sample;

import java.util.function.Supplier;

public class LazySample implements Sample {

  private final Supplier<Sample> supplier;
  private Sample initialized;

  public LazySample(Supplier<Sample> supplier) {
    this.supplier = supplier;
  }

  @Override
  public Object get() {
    if (initialized == null) {
      initialized = supplier.get();
    }
    return initialized.get();
  }

}
