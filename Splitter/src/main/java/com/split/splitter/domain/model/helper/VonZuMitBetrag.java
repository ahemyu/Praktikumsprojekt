package com.split.splitter.domain.model.helper;

import org.javamoney.moneta.Money;

public record VonZuMitBetrag(String zahlende, String kassierende, Money betrag) {

}
