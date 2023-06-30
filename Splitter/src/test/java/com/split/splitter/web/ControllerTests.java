package com.split.splitter.web;

import com.split.splitter.config.WebSecurityConfiguration;
import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import com.split.splitter.service.SplitterService;
import com.split.splitter.web.helper.AusgabeWrapper;
import com.split.splitter.web.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Import(WebSecurityConfiguration.class)


@WebMvcTest
public class ControllerTests {

    @MockBean
    SplitterService service;

    @Autowired
    MockMvc mvc;


    @WithMockOAuth2User(login = "bernd")
    @DisplayName("routing auf / funktioniert")
    @Test
    public void routingUebersicht() throws Exception {

        mvc.perform(get("/")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf gruppeErstellen funktioniert")
    @Test
    public void postGruppeErstellen() throws Exception {

        mvc.perform(post("/gruppeErstellen").param("gruppenName", "Gruppe").with(csrf())).andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        verify(service).createGruppe(eq("Gruppe"), eq("bernd"));
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("get auf gruppeDarstellen klappt")
    @Test
    public void getGruppeDarstellen() throws Exception {

        when(service.getGruppe(0)).thenReturn(Optional.of(new Gruppe("Gruppe", 0)));
        mvc.perform(get("/gruppe/{id}",0).with(csrf()));
        verify(service).getGruppe(0);
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf person hinzufügt klappt")
    @Test
    public void postPersonHinzufuegen() throws Exception {

        mvc.perform(post("/gruppe/{id}/personHinzufuegen", 0).param("personName", "otto").with(csrf())).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(service).addPersonToGruppe("otto", 0);
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("get auf ausgabe hinzufügt klappt")
    @Test
    public void getAusgabeHinzufuegen() throws Exception {

        when(service.getGruppe(0)).thenReturn(Optional.of(new Gruppe("Gruppe", 0)));
        mvc.perform(get("/gruppe/{id}/ausgabeHinzufuegen", 0).with(csrf())).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf /gruppe/{id}/ausgabeHinzufuegen funktioniert")
    @Test
    public void postAusgabeHinzufuegen() throws Exception {

        int id = 1;
        AusgabeWrapper ausgabeWrapper = new AusgabeWrapper("Person A", Set.of("Person A", "Person B"), 10.0, "Beschreibung");
        mvc.perform(post("/gruppe/{id}/ausgabeHinzufuegen", id)
                        .param("zahlendePerson", ausgabeWrapper.getZahlendePerson())
                        .param("teilnehmende", String.join(",", ausgabeWrapper.getTeilnehmende()))
                        .param("betrag", String.valueOf(ausgabeWrapper.getBetrag()))
                        .param("beschreibung", ausgabeWrapper.getBeschreibung())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppe/" + id));
        verify(service).addAusgabe(eq("Person A"), eq(Set.of("Person A", "Person B")), eq(Eur.of(10.0)), eq("Beschreibung"), eq(id));
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("get auf ausgabe anzeigen klappt")
    @Test
    public void getAusgabeAnzeigen() throws Exception {

        when(service.getGruppe(0)).thenReturn(Optional.of(new Gruppe("Otto", Set.of("Otto", "Peter"), 0)));
        mvc.perform(get("/gruppe/{id}/ausgabenAnzeigen", 0).with(csrf())).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf gruppe schließen klappt")
    @Test
    public void postGruppeSchliessen() throws Exception {

        mvc.perform(post("/gruppe/{id}/gruppeSchliessen", 1).with(csrf())).andExpect(MockMvcResultMatchers.redirectedUrl("/gruppe/1"));
        verify(service).gruppeSchliessen(1);
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("gruppe mit ungültigen namen wird nicht erstellt")
    @Test
    public void postGruppeErstellenWithInvalid() throws Exception {

        mvc.perform(post("/gruppeErstellen").param("gruppenName", "").with(csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppeErstellen"));

        verifyNoInteractions(service);
    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("person mit ungültigen namen wird nicht hinzugefügt")
    @Test
    public void postPersonHinzufuegenWithInvalid() throws Exception {

        mvc.perform(post("/gruppe/{id}/personHinzufuegen", 0).param("personName", "...")
                .with(csrf())).andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        verifyNoInteractions(service);

    }
    @WithMockOAuth2User(login = "bernd")
    @DisplayName("person mit ungültigen namen wird nicht hinzugefügt")
    @Test
    public void postPersonHinzufuegenWithInvalid2() throws Exception {

        mvc.perform(post("/gruppe/{id}/personHinzufuegen", 0).param("personName", ".p1.")
                .with(csrf())).andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        verifyNoInteractions(service);

    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf /gruppe/{id}/ausgabeHinzufuegen funktioniert nicht mit ungültiger zahlende")
    @Test
    public void postAusgabeHinzufuegenWithInvalidZahlende() throws Exception {
        int id = 1;
        AusgabeWrapper ausgabeWrapper = new AusgabeWrapper(null, Set.of("Person A", "Person B"), 10.0, "Beschreibung");
        mvc.perform(post("/gruppe/{id}/ausgabeHinzufuegen", id)
                        .param("zahlendePerson", ausgabeWrapper.getZahlendePerson())
                        .param("teilnehmende", String.join(",", ausgabeWrapper.getTeilnehmende()))
                        .param("betrag", String.valueOf(ausgabeWrapper.getBetrag()))
                        .param("beschreibung", ausgabeWrapper.getBeschreibung())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppe/" + id + "/ausgabeHinzufuegen"));
        verifyNoInteractions(service);

    }


    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf /gruppe/{id}/ausgabeHinzufuegen funktioniert nicht mit leerer teilnehmende")
    @Test
    public void postAusgabeHinzufuegenWithInvalidTeilnehmende() throws Exception {
        int id = 1;
        AusgabeWrapper ausgabeWrapper = new AusgabeWrapper("PersonA", Set.of(), 10.0, "Beschreibung");
        mvc.perform(post("/gruppe/{id}/ausgabeHinzufuegen", id)
                        .param("zahlendePerson", ausgabeWrapper.getZahlendePerson())
                        .param("teilnehmende", String.join(",", ausgabeWrapper.getTeilnehmende()))
                        .param("betrag", String.valueOf(ausgabeWrapper.getBetrag()))
                        .param("beschreibung", ausgabeWrapper.getBeschreibung())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppe/" + id + "/ausgabeHinzufuegen"));
        verifyNoInteractions(service);

    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf /gruppe/{id}/ausgabeHinzufuegen funktioniert nicht mit falschem betrag")
    @Test
    public void postAusgabeHinzufuegenWithInvalidBetrag() throws Exception {
        int id = 1;
        AusgabeWrapper ausgabeWrapper = new AusgabeWrapper("PersonA", Set.of("PersonA", "PersonB"), 10.0, "Beschreibung");
        mvc.perform(post("/gruppe/{id}/ausgabeHinzufuegen", id)
                        .param("zahlendePerson", ausgabeWrapper.getZahlendePerson())
                        .param("teilnehmende", String.join(",", ausgabeWrapper.getTeilnehmende()))
                        .param("betrag", "0,0,0")
                        .param("beschreibung", ausgabeWrapper.getBeschreibung())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppe/" + id + "/ausgabeHinzufuegen"));
        verifyNoInteractions(service);

    }

    @WithMockOAuth2User(login = "bernd")
    @DisplayName("post auf /gruppe/{id}/ausgabeHinzufuegen funktioniertnicht mit invaliden beschreibung")
    @Test
    public void postAusgabeHinzufuegenWithInvalidBeschreibung() throws Exception {
        int id = 1;
        AusgabeWrapper ausgabeWrapper = new AusgabeWrapper("PersonA", Set.of("PersonA", "PersonB"), 10.0, "");
        mvc.perform(post("/gruppe/{id}/ausgabeHinzufuegen", id)
                        .param("zahlendePerson", ausgabeWrapper.getZahlendePerson())
                        .param("teilnehmende", String.join(",", ausgabeWrapper.getTeilnehmende()))
                        .param("betrag", String.valueOf(ausgabeWrapper.getBetrag()))
                        .param("beschreibung", ausgabeWrapper.getBeschreibung())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppe/" + id + "/ausgabeHinzufuegen"));
        verifyNoInteractions(service);

    }


}
