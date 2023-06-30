package com.split.splitter.helper;

import javax.money.Monetary;
import org.javamoney.moneta.Money;


public class MoneyHelper {

  public static Money round(Money money) {
    return money.with(Monetary.getDefaultRounding());
  }

}


