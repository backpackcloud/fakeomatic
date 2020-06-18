package io.backpackcloud.fakeomatic;

import io.backpackcloud.fakeomatic.spi.Config;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseTest {

  protected Config                 config;
  protected Config.TemplateConfig  templateConfig;
  protected Config.GeneratorConfig generatorConfig;
  protected Config.EndpointConfig  endpointConfig;
  protected Random                 random;

  @BeforeEach
  public void init() {
    random = new Random();

    config = mock(Config.class);
    templateConfig = mock(Config.TemplateConfig.class);
    generatorConfig = mock(Config.GeneratorConfig.class);
    endpointConfig = mock(Config.EndpointConfig.class);

    when(config.template()).thenReturn(templateConfig);
    when(config.generator()).thenReturn(generatorConfig);
    when(config.endpoint()).thenReturn(endpointConfig);
  }

  protected void times(int times, Consumer<Integer> consumer) {
    for (int i = 0; i < times; i++) {
      consumer.accept(i);
    }
  }

  protected void times(int times, Runnable runnable) {
    for (int i = 0; i < times; i++) {
      runnable.run();
    }
  }

}
