package io.backpackcloud.fakeomatic;

import io.backpackcloud.fakeomatic.spi.Config;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseTest {

  protected Config                 config;
  protected Config.TemplateConfig  templateConfig;
  protected Config.GeneratorConfig generatorConfig;
  protected Config.EndpointConfig  endpointConfig;

  @BeforeEach
  public void init() {
    config = mock(Config.class);
    templateConfig = mock(Config.TemplateConfig.class);
    generatorConfig = mock(Config.GeneratorConfig.class);
    endpointConfig = mock(Config.EndpointConfig.class);

    when(config.template()).thenReturn(templateConfig);
    when(config.generator()).thenReturn(generatorConfig);
    when(config.endpoint()).thenReturn(endpointConfig);
  }

}
