package com.split.splitter.web.api.helper;

import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.parser.AusgabeAlsString;
import com.split.splitter.helper.parser.VonZuMitBetragAlsString;
import java.util.List;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class JsonValidation {

  public static ResponseEntity<Integer> isValidGruppe(GruppeFuerJson gruppe) {

    if (gruppe.personen() == null || gruppe.name() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    for (String s : gruppe.personen()) {
      if (!ValidGithubName.isValid(s)) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    if (Set.of(gruppe.personen()).isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (gruppe.name().isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  public static void createJsonGruppe(List<Gruppe> gruppen, JSONObject[] gruppenNamen) {

    for (Gruppe g : gruppen) {
      JSONObject gruppe = new JSONObject();
      gruppe.put("gruppe", g.getId());
      gruppe.put("name", g.getName());
      gruppe.put("personen", g.getPersonen());
      gruppenNamen[gruppen.indexOf(g)] = gruppe;
    }
  }

  public static JSONObject getJsonObjectForGruppe(Gruppe g) {

    JSONArray ausgaben = new JSONArray();


    JSONObject gruppeJson = new JSONObject();
    gruppeJson.put("gruppe", g.getId());
    gruppeJson.put("name", g.getName());
    gruppeJson.put("personen", g.getPersonen());
    gruppeJson.put("geschlossen", g.istGeschlossen());
    gruppeJson.put("ausgaben", ausgaben);

    for (AusgabeAlsString s : g.getAusgabenDarstellung()) {

      JSONObject zwischenspeicher = new JSONObject();

      zwischenspeicher.put("grund", s.beschreibung());
      zwischenspeicher.put("glaeubiger", s.zahlendePerson());
      zwischenspeicher.put("cent", s.toCent());
      zwischenspeicher.put("schuldner", s.getTeilnehmende());

      ausgaben.add(zwischenspeicher);
    }
    return gruppeJson;
  }

  public static ResponseEntity<String> ungueltigeAuslage(AuslageFuerJson auslageFuerJson,
                                                         Set<String> teilnehmende) {
    for (String s : auslageFuerJson.schuldner()) {
      if (!s.isEmpty()) {
        teilnehmende.add(s);
      }
    }
    if (teilnehmende.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (auslageFuerJson.glaeubiger().isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (auslageFuerJson.cent().isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  public static void createBalanceJsonArray(Gruppe gruppe, JSONArray gesamtesObjekt) {
    List<VonZuMitBetragAlsString> resultat = gruppe.getAusgleich();

    for (int i = 0; i < resultat.size(); i++) {

      JSONObject einzelneBalance = new JSONObject();

      VonZuMitBetragAlsString b = resultat.get(i);

      einzelneBalance.put("von", b.zahlende());
      einzelneBalance.put("an", b.kassierende());
      einzelneBalance.put("cents", b.toCent());

      gesamtesObjekt.add(einzelneBalance);
    }
  }

  public static ResponseEntity<String> gueltigeGruppenId(String id) {
    if (id == null || id.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    try {
      Integer.parseInt(id);
    } catch (NumberFormatException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return null;
  }
}

