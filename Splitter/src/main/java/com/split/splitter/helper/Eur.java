package com.split.splitter.helper;

import org.javamoney.moneta.Money;

public class Eur {

  public static Money of(double value) {
    return Money.of(value, "EUR");
  }
}
