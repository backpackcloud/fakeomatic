package com.backpackcloud.fakeomatic.sampler;

import com.backpackcloud.UnbelievableException;

import java.util.function.Supplier;

public class UndefinedSampleException extends UnbelievableException {

  public UndefinedSampleException() {
  }

  public UndefinedSampleException(String message) {
    super(message);
  }

  public UndefinedSampleException(String message, Throwable cause) {
    super(message, cause);
  }

  public UndefinedSampleException(Throwable cause) {
    super(cause);
  }

  public UndefinedSampleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public static Supplier<UndefinedSampleException> of(String sampleName) {
    return () -> new UndefinedSampleException(sampleName);
  }

}
