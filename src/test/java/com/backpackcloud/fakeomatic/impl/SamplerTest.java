package com.backpackcloud.fakeomatic.impl;

import com.backpackcloud.fakeomatic.sampler.Sampler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SamplerTest extends BaseTest {

  @Test
  public void testSomeMethod() {
    Sampler sampler = createSampler("faker.yaml");
    String letter = sampler.some("letter");
    assertNotNull(letter);
  }

  @Test
  public void testXgh() {
    Sampler sampler = createSampler("faker.yaml", () -> 0);
    String xghAxiom = sampler.some("xgh.title");
    assertEquals("I think therefore it's not XGH.", xghAxiom);
  }

}
