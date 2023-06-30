package com.split.splitter.web;


import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import com.split.splitter.service.SplitterService;
import com.split.splitter.web.helper.AusgabeWrapper;
import com.split.splitter.web.helper.GruppenName;
import com.split.splitter.web.helper.PersonName;
import java.util.*;
import javax.validation.Valid;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {


  private final SplitterService service;

  public WebController(SplitterService service) {
    this.service = service;
  }


  @GetMapping("/")
  public String index(OAuth2AuthenticationToken token, Model model) {

    String userName = token.getPrincipal().getAttribute("login");

//    System.out.println(service.findAlleGruppenVon(userName));

    model.addAttribute("gruppen", service.findAlleGruppenVon(userName));

    model.addAttribute("userName", userName);
    return "uebersicht";
  }

  @GetMapping("/gruppeErstellen")
  public String gruppeErstellen(Model m) {

    m.addAttribute("gruppenName", new GruppenName());

    return "gruppeErstellen";
  }


  @PostMapping("/gruppeErstellen")
  public String gruppeErstellen(OAuth2AuthenticationToken token,
                                @Valid GruppenName gruppenName,
                                BindingResult bindingResult, Model m,
                                RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      System.err.println("Fehler");
      redirectAttributes.addFlashAttribute("errornachricht", "Gruppenname darf nicht leer sein");
      return "redirect:/gruppeErstellen";
    }

    String userName = token.getPrincipal().getAttribute("login");

    service.createGruppe(gruppenName.getGruppenName(), userName);

    return "redirect:/";
  }

  @GetMapping("/gruppe/{id}")
  public String gruppeDarstellen(@PathVariable int id, Model m) {

    Optional<Gruppe> gruppe = service.getGruppe(id);

    Gruppe g = gruppe.orElseGet(() -> null);
    if (g == null) {
      return "redirect:/";
    }

    m.addAttribute("darzustellendeGruppe", g);

    return "gruppe";
  }

  @PostMapping("/gruppe/{id}/personHinzufuegen")
  public String postPersonHinzufuegen(@PathVariable int id,
                                      @Valid PersonName personName,
                                      BindingResult bindingResult,
                                      Model m, RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute(
              "errornachricht", "Name muss ein valider GitHub Name sein");
      return "redirect:/gruppe/" + id;
    }

    service.addPersonToGruppe(personName.getPersonName(), id);

    return "redirect:/gruppe/" + id;
  }

  @GetMapping("/gruppe/{id}/ausgabeHinzufuegen")
  public String getAusgabeHinzufuegen(@PathVariable int id, Model m) {

    AusgabeWrapper ausgabeWrapper = new AusgabeWrapper();

    Optional<Gruppe> gruppe = service.getGruppe(id);

    Gruppe g = gruppe.orElseGet(() -> null);
    if (g == null) {
      return "redirect:/";
    }

    m.addAttribute("darzustellendeGruppe", g);
    m.addAttribute("ausgabe", ausgabeWrapper);

    return "/ausgabeHinzufuegen";

  }


  @PostMapping("/gruppe/{id}/ausgabeHinzufuegen")
  public String postAusgabeHinzufuegen(@PathVariable int id, @Valid AusgabeWrapper ausgabeWrapper,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("errornachricht", "Ausgabe muss Gläubiger, Schuldner,"
              + " Beschreibung und einen gültigen Betrag enthalten");
      return "redirect:/gruppe/" + id + "/ausgabeHinzufuegen";
    }

    service.addAusgabe(
            ausgabeWrapper.getZahlendePerson(),
            ausgabeWrapper.getTeilnehmende(),
            Eur.of(ausgabeWrapper.getBetrag()),
            ausgabeWrapper.getBeschreibung(),
            id);

    return "redirect:/gruppe/" + id;

  }

  @GetMapping("/gruppe/{id}/ausgabenAnzeigen")
  public String getAusgabenAnzeigen(@PathVariable int id,
                                    Model m,
                                    OAuth2AuthenticationToken token) {

    Optional<Gruppe> gruppe = service.getGruppe(id);

    Gruppe g = gruppe.orElseGet(() -> null);

    if (g == null) {
      return "redirect:/";
    }

    String userName = token.getPrincipal().getAttribute("login");

    m.addAttribute("alleAusgabenMitBeteiligung", g.alleAusgabenMitBeteiligung(userName));

    m.addAttribute("darzustellendeGruppe", g);

    return "/ausgabenAnzeigen";
  }

  @PostMapping("/gruppe/{id}/gruppeSchliessen")
  public String postGruppeSchliessen(@PathVariable int id) {

    service.gruppeSchliessen(id);

    return "redirect:/gruppe/" + id;
  }



}
