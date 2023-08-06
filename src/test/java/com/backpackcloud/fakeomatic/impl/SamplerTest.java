package com.backpackcloud.fakeomatic.impl;

import com.backpackcloud.fakeomatic.sampler.Sampler;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SamplerTest extends BaseTest {

  Sampler sampler = createSampler("faker.yaml");

  @Test
  public void testSomeMethod() {
    String letter = sampler.some("letter");
    assertNotNull(letter);
  }

  @Test
  public void testXgh() {
    Sampler s = Sampler.defaultSampler(new Random());
    assertNotNull(s.some("xgh.title"));
  }

}
