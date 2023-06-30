package com.split.splitter.helper.parser;

import java.util.Set;
import org.javamoney.moneta.Money;


public record AusgabeAlsString(
        String zahlendePerson, String teilnehmende, String betrag, String beschreibung) {

  public static AusgabeAlsString of(
          String zahlendePerson, Set<String> teilnehmende, Money betrag, String beschreibung) {

    String teilnehmendeFormat = String.join(", ", teilnehmende);
    String betragString = betrag.getNumberStripped().toPlainString() + "€";

    return new AusgabeAlsString(zahlendePerson, teilnehmendeFormat, betragString, beschreibung);
  }


  public String toCent() {
    String betragString = betrag.substring(0, betrag.length() - 1);
    double d = Double.parseDouble(betragString);
    int cent = (int) (d * 100);
    return String.valueOf(cent);

  }

  public String[] getTeilnehmende() {
    String[] teilnehmendeArray = teilnehmende.split(", ");
    return teilnehmendeArray;
  }
}
