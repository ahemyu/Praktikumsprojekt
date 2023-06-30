package com.split.splitter.domain.model;

import com.split.splitter.annotations.AggregateRoot;
import com.split.splitter.domain.model.helper.PersonMitBetrag;
import com.split.splitter.domain.model.helper.VonZuMitBetrag;
import com.split.splitter.helper.Eur;
import com.split.splitter.helper.MoneyHelper;
import com.split.splitter.helper.parser.AusgabeAlsString;
import com.split.splitter.helper.parser.AusgabeAlsStringMitPers;
import com.split.splitter.helper.parser.VonZuMitBetragAlsString;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.javamoney.moneta.Money;

import static com.split.splitter.domain.model.helper.AusgleichHelper.findePerfektenAusgleichFuerMax;
import static com.split.splitter.domain.model.helper.AusgleichHelper.findePerfektenAusgleichFuerMin;
import static com.split.splitter.domain.model.helper.AusgleichHelper.minMaxEquals0;
import static com.split.splitter.domain.model.helper.AusgleichHelper.gleicheMaxMitMinAus;


@AggregateRoot
public class Gruppe {

  private final Integer id;

  private final String name;

  private Set<String> personen = new HashSet<>();

  private boolean istGeschlossen;

  private List<Ausgabe> ausgabenDarstellung = new ArrayList<>();


  public Gruppe(String name, Set<String> personen, Integer id) {
    this.name = name;
    this.personen = personen;
    this.id = id;
  }

  public Gruppe(String name, Integer id) {
    this.name = name;
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public Set<String> getPersonen() {
    return personen;
  }

  public Integer getId() {
    return id;
  }

  public List<AusgabeAlsString> getAusgabenDarstellung() {
    List<AusgabeAlsString> ausgaben = new ArrayList<>();
    for (Ausgabe ausgabe : ausgabenDarstellung
    ) {
      ausgaben.add(AusgabeAlsString.of(
              ausgabe.zahlendePerson(), ausgabe.teilnehmende(),
              ausgabe.betrag(), ausgabe.beschreibung()));
    }
    return ausgaben;
  }

  public boolean istGeschlossen() {
    return istGeschlossen;
  }

  public boolean ausgabeSchonGetaetigt() {
    return !ausgabenDarstellung.isEmpty();
  }


  public String addPersonInGruppe(String personName) {

    if (istGeschlossen) {
      return null;

    } else if (ausgabeSchonGetaetigt()) {
      return null;

    } else {


      personen.add(personName);
      return personName;
    }

  }

  private List<VonZuMitBetrag> getAlleTeilTransaktionen(List<Ausgabe> ausgaben) {


    List<VonZuMitBetrag> alleTeilTransaktionen = new ArrayList<>();

    for (Ausgabe ausgabe : ausgaben) {

      int teilnehmendePersonen = ausgabe.teilnehmende().size();

      Money aktuellerBetrag = MoneyHelper.round(ausgabe.betrag().divide(teilnehmendePersonen));

      ausgabe.teilnehmende().forEach(
              e -> alleTeilTransaktionen.add(new VonZuMitBetrag(
                      ausgabe.zahlendePerson(), e, aktuellerBetrag)));

    }

    return alleTeilTransaktionen;
  }

  public void addAusgabe(String person,
                         Set<String> teilnehmende,
                         Money betrag,
                         String beschreibung) {

    Ausgabe ausgabe = new Ausgabe(person, teilnehmende, betrag, beschreibung);

    if (!istGeschlossen) {

      ausgabenDarstellung.add(ausgabe);

    }
  }

  private List<PersonMitBetrag> getTransaktionenFuer(String pers) {

    List<PersonMitBetrag> alleTransaktionenVonPerson =
            Stream.concat(
                            personBezahlt(pers).stream(), personSchuldet(pers).stream())
                    .collect(Collectors.toList());

    return alleTransaktionenVonPerson;

  }

  public List<VonZuMitBetragAlsString> getAusgleich() {

    List<PersonMitBetrag> alleTransaktionen = new ArrayList<>();

    for (String p : personen) {

      alleTransaktionen.addAll(getTransaktionenFuer(p));

    }

    alleTransaktionen = addiereTransaktionFuerAlle(alleTransaktionen);

    return berechneAusgleich(alleTransaktionen);
  }

  private List<PersonMitBetrag> addiereTransaktionFuerAlle(
          List<PersonMitBetrag> alleTransaktionen) {

    Map<String, Money> helperMap = new HashMap<>();

    personen.forEach(e -> helperMap.put(e, Money.of(0, "EUR")));

    alleTransaktionen.forEach(
            e -> helperMap.put(e.name(), helperMap.get(
                    e.name()).add(e.betrag())));

    List<PersonMitBetrag> addierteTransaktionen = new ArrayList<>();

    helperMap.forEach((k, v) -> addierteTransaktionen.add(new PersonMitBetrag(k, v)));

    return addierteTransaktionen;
  }


  private List<VonZuMitBetragAlsString> berechneAusgleich(
          List<PersonMitBetrag> addierteTransaktionen) {

    List<VonZuMitBetrag> fertigerAusgleich = new ArrayList<>();

    Map<String, Money> mapFuerMinimal = new HashMap<>();

    addierteTransaktionen.forEach(e -> mapFuerMinimal.put(e.name(), e.betrag()));

    int schleifendurchlaufe = addierteTransaktionen.size();


    for (int i = 0; i < schleifendurchlaufe; i++) {

      if (mapFuerMinimal.size() == 0) {
        break;
      }

      addierteTransaktionen.clear();
      mapFuerMinimal.forEach(
              (k, v) -> addierteTransaktionen.add(addierteTransaktionen.size(),
                      new PersonMitBetrag(k, v)));


      PersonMitBetrag min = addierteTransaktionen.stream()
              .min(Comparator.comparing(PersonMitBetrag::betrag)).get();
      PersonMitBetrag max = addierteTransaktionen.stream()
              .max(Comparator.comparing(PersonMitBetrag::betrag)).get();


      if (minMaxEquals0(min, max)) {

        break;
      }


      findePerfektenAusgleichFuerMax(addierteTransaktionen, fertigerAusgleich, mapFuerMinimal, max);


      if (addierteTransaktionen.size() == 0) {
        break;
      }

      min = addierteTransaktionen.stream().min(Comparator.comparing(PersonMitBetrag::betrag)).get();
      max = addierteTransaktionen.stream().max(Comparator.comparing(PersonMitBetrag::betrag)).get();

      findePerfektenAusgleichFuerMin(addierteTransaktionen, fertigerAusgleich, mapFuerMinimal, min);

      if (addierteTransaktionen.size() == 0) {
        break;
      }

      min = addierteTransaktionen.stream().min(Comparator.comparing(PersonMitBetrag::betrag)).get();
      max = addierteTransaktionen.stream().max(Comparator.comparing(PersonMitBetrag::betrag)).get();


      gleicheMaxMitMinAus(fertigerAusgleich, mapFuerMinimal, min, max);
    }


    fertigerAusgleich = fertigerAusgleich.stream()
            .filter(e -> !e.zahlende().equals(e.kassierende())).collect(Collectors.toList());

    fertigerAusgleich = fertigerAusgleich.stream()
            .filter(e -> !e.betrag().equals(Eur.of(0))).collect(Collectors.toList());


    List<VonZuMitBetragAlsString> fertigerAusgleichAlsString = new ArrayList<>();
    for (VonZuMitBetrag b : fertigerAusgleich) {
      fertigerAusgleichAlsString.add(
              VonZuMitBetragAlsString.of(b.zahlende(), b.kassierende(), b.betrag()));
    }
    return fertigerAusgleichAlsString;


  }


  private List<PersonMitBetrag> personBezahlt(String pers) {

    List<PersonMitBetrag> personBezahlt = getAlleTeilTransaktionen(ausgabenDarstellung).stream()
            .filter(e -> e.zahlende().equals(pers))
            .map(e -> new PersonMitBetrag(e.kassierende(), Money.of(0, "EUR").subtract(e.betrag())))
            .collect(Collectors.toList());

    return personBezahlt;
  }

  private List<PersonMitBetrag> personSchuldet(String pers) {

    List<PersonMitBetrag> personSchuldet = getAlleTeilTransaktionen(ausgabenDarstellung).stream()
            .filter(e -> e.kassierende().equals(pers))
            .map(e -> new PersonMitBetrag(e.zahlende(), e.betrag()))
            .collect(Collectors.toList());

    return personSchuldet;
  }

  public void gruppeSchliessen() {
    istGeschlossen = true;
  }


  public List<AusgabeAlsStringMitPers> alleAusgabenMitBeteiligung(String personenName) {

    List<AusgabeAlsStringMitPers> alleAusgabenMitBeteiligungAlsStringMitPers = new ArrayList<>();


    for (int i = 0; i < ausgabenDarstellung.size(); i++) {

      boolean beteiligt = false;

      if (ausgabenDarstellung.get(i).zahlendePerson().equals(personenName)) {
        beteiligt = true;
      }

      if (ausgabenDarstellung.get(i).teilnehmende().contains(personenName)) {

        beteiligt = true;
      }

      Ausgabe zwischenspeicher = ausgabenDarstellung.get(i);

      alleAusgabenMitBeteiligungAlsStringMitPers.add(
              AusgabeAlsStringMitPers.of(zwischenspeicher.zahlendePerson(),
                      zwischenspeicher.teilnehmende(),
                      zwischenspeicher.betrag(),
                      zwischenspeicher.beschreibung(),
                      beteiligt));


    }


    return alleAusgabenMitBeteiligungAlsStringMitPers;


  }

  @Override
  public String toString() {
    return "Gruppe{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", personen=" + personen
            + ", istGeschlossen=" + istGeschlossen
            + ", ausgabenDarstellung=" + ausgabenDarstellung
            + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Gruppe gruppe = (Gruppe) o;
    return istGeschlossen == gruppe.istGeschlossen
            && Objects.equals(id, gruppe.id)
            && name.equals(gruppe.name)
            && personen.equals(gruppe.personen)
            && ausgabenDarstellung.equals(gruppe.ausgabenDarstellung);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, personen, istGeschlossen, ausgabenDarstellung);
  }
}