package com.split.splitter.web.api;

import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import com.split.splitter.service.SplitterService;
import com.split.splitter.web.api.helper.AuslageFuerJson;
import com.split.splitter.web.api.helper.GruppeFuerJson;
import com.split.splitter.web.api.helper.JsonValidation;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.javamoney.moneta.Money;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/api")
@RestController
public class JsonController {

  private final SplitterService service;

  public JsonController(SplitterService splitterService) {
    this.service = splitterService;
  }

  @PostMapping("/gruppen")
  public ResponseEntity<Integer> addGruppe(@RequestBody GruppeFuerJson gruppe) {


    ResponseEntity<Integer> badRequest = JsonValidation.isValidGruppe(gruppe);
    if (badRequest != null) {
      return badRequest;
    }

    int id = service.createGruppeForJson(gruppe.name(), Set.of(gruppe.personen()));

    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @GetMapping("/user/{login}/gruppen")
  public ResponseEntity<JSONObject[]> getGruppen(@PathVariable String login) {

    List<Gruppe> gruppen = service.findAlleGruppenVon(login);

    JSONObject[] gruppenNamen = new JSONObject[gruppen.size()];
    JsonValidation.createJsonGruppe(gruppen, gruppenNamen);

    return new ResponseEntity<>(gruppenNamen, HttpStatus.OK);
  }


  @GetMapping("/gruppen/{id}")
  public ResponseEntity<JSONObject> getGruppe(@PathVariable String id) {

    ResponseEntity<String> notFound = JsonValidation.gueltigeGruppenId(id);
    if (notFound != null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Optional<Gruppe> gruppe = service.getGruppe(Integer.parseInt(id));
    Gruppe g = gruppe.orElseGet(() -> null);
    if (g == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    JSONObject gruppeJson = JsonValidation.getJsonObjectForGruppe(g);

    return new ResponseEntity<>(gruppeJson, HttpStatus.OK);
  }


  @PostMapping("/gruppen/{id}/schliessen")
  public ResponseEntity<String> postGruppeSchliessen(@PathVariable String id) {

    ResponseEntity<String> notFound = JsonValidation.gueltigeGruppenId(id);
    if (notFound != null) {
      return notFound;
    }
    Optional<Gruppe> gruppe = service.getGruppe(Integer.parseInt(id));
    Gruppe g = gruppe.orElseGet(() -> null);
    if (g == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //g.gruppeSchliessen(); //eigentlich falsch
    service.gruppeSchliessen(Integer.parseInt(id));

    return new ResponseEntity<>(HttpStatus.OK);

  }

  @PostMapping("/gruppen/{id}/auslagen")
  public ResponseEntity<String> postAuslagen(@PathVariable String id,
                                             @RequestBody AuslageFuerJson auslageFuerJson) {

    ResponseEntity<String> notFound = JsonValidation.gueltigeGruppenId(id);
    if (notFound != null) {
      return notFound;
    }

    if (auslageFuerJson.schuldner() == null
            || auslageFuerJson.glaeubiger() == null
            || auslageFuerJson.cent() == null
            || auslageFuerJson.grund() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    final String person = auslageFuerJson.glaeubiger();
    final Money betrag = Eur.of(Double.parseDouble(auslageFuerJson.cent()));
    final String beschreibung = auslageFuerJson.grund();

    Set<String> teilnehmende = new HashSet<>();
    ResponseEntity<String> badRequest =
            JsonValidation.ungueltigeAuslage(auslageFuerJson, teilnehmende);
    if (badRequest != null) {
      return badRequest;
    }

    Optional<Gruppe> g = service.getGruppe(Integer.parseInt(id));
    Gruppe gruppe = g.orElseGet(() -> null);

    if (gruppe == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (gruppe.istGeschlossen()) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    service.addAusgabe(person, teilnehmende, betrag, beschreibung, Integer.parseInt(id));

    return new ResponseEntity<>(HttpStatus.CREATED);

  }

  @GetMapping("/gruppen/{id}/ausgleich")
  public ResponseEntity<JSONArray> getAusgleich(@PathVariable String id) {

    ResponseEntity<String> notFound = JsonValidation.gueltigeGruppenId(id);
    if (notFound != null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Optional<Gruppe> g = service.getGruppe(Integer.parseInt(id));
    Gruppe gruppe = g.orElseGet(() -> null);

    if (gruppe == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    JSONArray gesamtesObjekt = new JSONArray();

    JsonValidation.createBalanceJsonArray(gruppe, gesamtesObjekt);

    return new ResponseEntity<>(gesamtesObjekt, HttpStatus.OK);


  }


}

