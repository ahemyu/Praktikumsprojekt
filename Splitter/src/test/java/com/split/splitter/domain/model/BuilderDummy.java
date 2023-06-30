package com.split.splitter.domain.model;

import com.split.splitter.helper.Eur;

import java.util.HashSet;
import java.util.Set;

public class BuilderDummy {

    public static Gruppe createGruppe(int anzahlPersonen){

            Set<String> personen = new HashSet<>();

            for (int i = 0; i < anzahlPersonen; i++) {
                personen.add("Person " + i);
            }

            return new Gruppe("Gruppe", personen, 1);
    }

    public static void fuegeAusgabeFuerAlle(Gruppe gruppe, double betrag){
        gruppe.addAusgabe("Person 0",
                gruppe.getPersonen(), Eur.of(betrag), "alle nehmen teil");

    }


}
