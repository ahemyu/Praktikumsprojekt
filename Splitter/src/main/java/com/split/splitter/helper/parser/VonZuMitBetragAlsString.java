package com.split.splitter.helper.parser;

import org.javamoney.moneta.Money;

public record VonZuMitBetragAlsString(
        String zahlende,
        String kassierende,
        String betrag
) {

  public static VonZuMitBetragAlsString of(String zahlende, String kassierende, Money betrag) {

    String betragString = betrag.getNumberStripped().toPlainString() + "€";

    return new VonZuMitBetragAlsString(zahlende, kassierende, betragString);

  }

  public String toCent() {
    String betragString = betrag.substring(0, betrag.length() - 1);
    double d = Double.parseDouble(betragString);
    int cent = (int) (d * 100);
    return String.valueOf(cent);

  }

}