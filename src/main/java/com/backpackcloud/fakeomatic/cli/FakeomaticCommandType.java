package com.backpackcloud.fakeomatic.cli;

import com.backpackcloud.cli.CommandType;

public enum FakeomaticCommandType implements CommandType {

  SAMPLE("Sample Generation Commands");

  private final String description;

  FakeomaticCommandType(String description) {
    this.description = description;
  }

  public String description() {
    return this.description;
  }


}
