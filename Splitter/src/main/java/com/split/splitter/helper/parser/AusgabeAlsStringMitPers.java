package com.split.splitter.helper.parser;

import java.util.Set;
import org.javamoney.moneta.Money;



public record AusgabeAlsStringMitPers(
        String zahlendePerson, String teilnehmende, String betrag, String beschreibung,
                                      boolean beteiligt) {

  public static AusgabeAlsStringMitPers of(
          String zahlendePerson, Set<String> teilnehmende,
          Money betrag, String beschreibung, boolean beteiligt) {

    String teilnehmendeFormat = String.join(", ", teilnehmende);
    String betragString = betrag.getNumberStripped().toPlainString() + "€";

    return new AusgabeAlsStringMitPers(zahlendePerson,
            teilnehmendeFormat, betragString, beschreibung, beteiligt);

  }

}

