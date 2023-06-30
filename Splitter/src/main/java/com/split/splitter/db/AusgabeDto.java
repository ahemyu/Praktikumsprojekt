package com.split.splitter.db;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "ausgabe")
public record AusgabeDto(String zahlendePerson,
                         String [] schuldner,
                         double betrag,
                         String beschreibung) {
}
