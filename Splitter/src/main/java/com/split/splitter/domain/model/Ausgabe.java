package com.split.splitter.domain.model;

import java.util.Set;
import org.javamoney.moneta.Money;

record Ausgabe(String zahlendePerson, Set<String> teilnehmende, Money betrag, String beschreibung) {

}