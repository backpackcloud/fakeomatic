package com.backpackcloud.fakeomatic.sampler;

import java.util.random.RandomGenerator;

public class RNG implements RandomGenerator {

  private RandomGenerator delegate;

  public RNG(RandomGenerator delegate) {
    this.delegate = delegate;
  }

  public void change(RandomGenerator newRng) {
    this.delegate = newRng;
  }

  @Override
  public long nextLong() {
    return this.delegate.nextLong();
  }

}
