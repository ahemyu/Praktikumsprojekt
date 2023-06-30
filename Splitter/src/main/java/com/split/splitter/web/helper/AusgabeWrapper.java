package com.split.splitter.web.helper;

import com.split.splitter.web.helper.validation.ValidMoney;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class AusgabeWrapper {

  @NotBlank(message = "Zahlende Person darf nicht leer sein")
  private String zahlendePerson;
  @NotEmpty(message = "Teilnehmende Personen dürfen nicht leer sein")
  private Set<String> teilnehmende;

  @ValidMoney
  private double betrag;

  @NotBlank
  private String beschreibung;

  public String getZahlendePerson() {
    return zahlendePerson;
  }


  public void setZahlendePerson(String zahlendePerson) {
    this.zahlendePerson = zahlendePerson;
  }

  public Set<String> getTeilnehmende() {
    return teilnehmende;
  }

  public void setTeilnehmende(Set<String> teilnehmende) {
    this.teilnehmende = teilnehmende;
  }

  public double getBetrag() {
    return betrag;
  }

  public void setBetrag(double betrag) {
    this.betrag = betrag;
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public void setBeschreibung(String beschreibung) {
    this.beschreibung = beschreibung;
  }

  public AusgabeWrapper(String zahlendePerson,
                        Set<String> teilnehmende,
                        double betrag,
                        String beschreibung) {

    this.zahlendePerson = zahlendePerson;
    this.teilnehmende = teilnehmende;
    this.betrag = betrag;
    this.beschreibung = beschreibung;
  }


  public AusgabeWrapper() {
    zahlendePerson = "";
    teilnehmende = new HashSet<String>();
    betrag = 0;
    beschreibung = "";
  }

  @Override
  public String toString() {
    return "AusgabeWrapper{"
            + "zahlendePerson='"
            + zahlendePerson + '\''
            + ", teilnehmende=" + teilnehmende
            + ", betrag=" + betrag
            + ", beschreibung='" + beschreibung + '\'' + '}';
  }

}

