package io.backpackcloud.fakeomatic.spi.samples;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.spi.Sample;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@RegisterForReflection
public class FileSample implements Sample {

  private final Sample listSample;

  @JsonCreator
  public FileSample(@JsonProperty("file") String file,
                    @JsonProperty("charset") String charset) throws IOException {
    Charset fileCharset = Charset.forName(charset == null ? "UTF-8" : charset);

    List<String> strings = Files.readAllLines(Paths.get(URI.create(file)), fileCharset);

    this.listSample = new ListSample(strings);
  }

  @Override
  public String get(Random random) {
    return listSample.get(random);
  }

}
