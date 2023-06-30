package com.split.splitter.domain.model;

import com.split.splitter.helper.Eur;
import com.split.splitter.helper.parser.AusgabeAlsString;
import com.split.splitter.helper.parser.AusgabeAlsStringMitPers;
import com.split.splitter.helper.parser.VonZuMitBetragAlsString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.split.splitter.domain.model.BuilderDummy.createGruppe;
import static org.assertj.core.api.Assertions.assertThat;

public class DomainTests {


    //Shortcuts for scenario tests
    String a = "Person 0";
    String b = "Person 1";
    String c = "Person 2";
    String d = "Person 3";
    String e = "Person 4";
    String f = "Person 5";
    String g = "Person 6";


    @DisplayName("zu Gruppe hinzufügen klappt")
    @Test
    void personHinzufügenKlappt() {

        Gruppe gruppe = createGruppe(1);

        gruppe.addPersonInGruppe("Person 1");

        assertThat(gruppe.getPersonen()).containsExactly("Person 0", "Person 1");

    }

    @DisplayName("Ausgabe hinzufügen klappt")
    @Test
    void ausgabeHinzufügenKlappt() {

        Gruppe g = BuilderDummy.createGruppe(2);
        g.addAusgabe("Person 0", g.getPersonen(), Eur.of(100), "alle nehmen teil");

        assertThat(g.getAusgabenDarstellung()).containsExactly(AusgabeAlsString.of("Person 0", g.getPersonen(), Eur.of(100), "alle nehmen teil"));

    }

    @DisplayName("hinzufügen von Person klappt nicht, wenn Gruppe geschlossen")
    @Test
    void hinzufügenKlapptNichtGruppeGeschlossen() {

        Gruppe g = BuilderDummy.createGruppe(2);
        String peter = "Peter";

        g.gruppeSchliessen();
        g.addPersonInGruppe(peter);

        assertThat(g.getPersonen()).containsExactly("Person 0", "Person 1");

    }

    @DisplayName("hinzufügen von Person klappt nicht, wenn Ausgabe getätigt")
    @Test
    void hinzufügenKlapptNichtAusgabeGetätigt() {
        String peter = "Peter";
        Gruppe g = BuilderDummy.createGruppe(2);
        BuilderDummy.fuegeAusgabeFuerAlle(g, 100);

        g.addPersonInGruppe(peter);

        assertThat(g.getPersonen()).containsExactly("Person 0", "Person 1");

    }




    @DisplayName("Szenario 1")
    @Test
    public void szenario1() {

        Gruppe gruppe = BuilderDummy.createGruppe(2);
        gruppe.addAusgabe(a, Set.of(a, b), Eur.of(10), "alle nehmen teil");
        gruppe.addAusgabe(a, Set.of(a, b), Eur.of(20), "alle nehmen teil");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

        assertThat(resultat).contains(VonZuMitBetragAlsString.of(b, a, Eur.of(15)));
        assertThat(resultat).hasSize(1);

    }


    @DisplayName("Szenario 2")
    @Test
    public void szenario2() {

        Gruppe gruppe = BuilderDummy.createGruppe(2);
        gruppe.addAusgabe(a, Set.of(a, b), Eur.of(10), "alle nehmen teil");
        gruppe.addAusgabe(b, Set.of(a, b), Eur.of(20), "alle nehmen teil");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

        assertThat(resultat).contains(VonZuMitBetragAlsString.of(a, b, Eur.of(5)));
        assertThat(resultat).hasSize(1);

    }


    @DisplayName("Szenario 3")
    @Test
    public void szenario3() {

        Gruppe gruppe = BuilderDummy.createGruppe(2);
        gruppe.addAusgabe(a, Set.of(b), Eur.of(10), "nur B nimmt teil");
        gruppe.addAusgabe(a, Set.of(a, b), Eur.of(20), "alle nehmen teil");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

        assertThat(resultat).contains(VonZuMitBetragAlsString.of(b, a, Eur.of(20)));
        assertThat(resultat).hasSize(1);

    }

    @DisplayName("Szenario 4")
    @Test
    public void szenario4() {

        Gruppe gruppe = BuilderDummy.createGruppe(3);
        gruppe.addAusgabe(a, Set.of(a, b), Eur.of(10), "0 und 1 nehmen teil");
        gruppe.addAusgabe(b, Set.of(b, c), Eur.of(10), "1 und 2 nehmen teil");
        gruppe.addAusgabe(c, Set.of(c, a), Eur.of(10), "2 und 0 nehmen teil");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

        assertThat(gruppe.getAusgleich()).isEmpty();

    }


    @DisplayName("Szenario5")
    @Test
    public void szenario5() {

        Gruppe gruppe = BuilderDummy.createGruppe(3);
        gruppe.addAusgabe(a, Set.of(a, b, c), Eur.of(60), "alle nehmen teil, anton gibt aus");
        gruppe.addAusgabe(b, Set.of(a, b, c), Eur.of(30), "alle nehmen teil, berta gibt aus");
        gruppe.addAusgabe(c, Set.of(b, c), Eur.of(100), "christian und berta nehmen teil, christian gibt aus");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

        assertThat(resultat)
                .contains(VonZuMitBetragAlsString.of(b, a, Eur.of(30)))
                .contains(VonZuMitBetragAlsString.of(b, c, Eur.of(20)));
        assertThat(resultat).hasSize(2);

    }

    @DisplayName("Szenario6")
    @Test
    public void szenario6() {
        Gruppe gruppe = BuilderDummy.createGruppe(6);
        gruppe.addAusgabe(a, gruppe.getPersonen(), Eur.of(564), "Hotelzimmer");
        gruppe.addAusgabe(b, Set.of(a, b), Eur.of(38.58), "Benzin Hinweg");
        gruppe.addAusgabe(b, Set.of(a, b, d), Eur.of(38.58), "Benzin Rückweg");
        gruppe.addAusgabe(c, Set.of(c, e, f), Eur.of(82.11), "Benzin");
        gruppe.addAusgabe(d, gruppe.getPersonen(), Eur.of(96), "Städtetour");
        gruppe.addAusgabe(f, Set.of(b, e, f), Eur.of(95.37), "Theater");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

        assertThat(resultat)
                .contains(VonZuMitBetragAlsString.of(b, a, Eur.of(96.78)))
                .contains(VonZuMitBetragAlsString.of(c, a, Eur.of(55.26)))
                .contains(VonZuMitBetragAlsString.of(d, a, Eur.of(26.86)))
                .contains(VonZuMitBetragAlsString.of(e, a, Eur.of(169.16)))
                .contains(VonZuMitBetragAlsString.of(f, a, Eur.of(73.79)));
        assertThat(resultat).hasSize(5);

    }

    @DisplayName("Szenario7")
    @Test
    public void szenario7() {
        Gruppe gruppe = createGruppe(7);
        gruppe.addAusgabe(d, Set.of(d, f), Eur.of(20), "");
        gruppe.addAusgabe(g, Set.of(b), Eur.of(10), "Benzin Hinweg");
        gruppe.addAusgabe(e, Set.of(a, c, e), Eur.of(75), "Benzin Rückweg");
        gruppe.addAusgabe(f, Set.of(a, f), Eur.of(50), "Benzin");
        gruppe.addAusgabe(e, Set.of(d), Eur.of(40), "Städtetour");
        gruppe.addAusgabe(f, Set.of(b, f), Eur.of(40), "Theater");
        gruppe.addAusgabe(f, Set.of(c), Eur.of(5), "Theater");
        gruppe.addAusgabe(g, Set.of(a), Eur.of(30), "Theater");

        List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();


        assertThat(resultat)
                .contains(VonZuMitBetragAlsString.of(a, f, Eur.of(40)))
                .contains(VonZuMitBetragAlsString.of(a, g, Eur.of(40)))
                .contains(VonZuMitBetragAlsString.of(b, e, Eur.of(30)))
                .contains(VonZuMitBetragAlsString.of(c, e, Eur.of(30)))
                .contains(VonZuMitBetragAlsString.of(d, e, Eur.of(30)));
        assertThat(resultat).hasSize(5);

    }


    @Test
    @DisplayName("alleAusgabenMitBeteiligung klappt")
    void alleAusgabenMitBeteilugungKlappt(){

        Gruppe gruppe = createGruppe(3);
        gruppe.addAusgabe(a, Set.of( b, c), Eur.of(60), "b und c nehmen teil, a gibt aus");
        gruppe.addAusgabe(b, Set.of(a, b, c), Eur.of(30), "alle nehgmen teil, b gibt aus");
        gruppe.addAusgabe(c, Set.of(b, c), Eur.of(100), "b und c nehmen teil, c gibt aus");

        List<AusgabeAlsStringMitPers> resultat = gruppe.alleAusgabenMitBeteiligung(a);

        assertThat(resultat)
                .contains(AusgabeAlsStringMitPers.of(a, Set.of( b, c), Eur.of(60), "b und c nehmen teil, a gibt aus", true))
                .contains(AusgabeAlsStringMitPers.of(b, Set.of(a, b, c), Eur.of(30), "alle nehgmen teil, b gibt aus", true))
                .contains(AusgabeAlsStringMitPers.of(c, Set.of(b, c), Eur.of(100), "b und c nehmen teil, c gibt aus", false));
        assertThat(resultat).hasSize(3);

    }

}
