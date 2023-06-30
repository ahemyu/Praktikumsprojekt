package com.split.splitter.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import com.split.splitter.service.SplitterService;
import com.split.splitter.web.api.helper.AuslageFuerJson;
import com.split.splitter.web.api.helper.GruppeFuerJson;
import com.split.splitter.web.api.helper.JsonValidation;
import com.split.splitter.web.helper.WithMockOAuth2User;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(com.split.splitter.config.WebSecurityConfiguration.class)
@WebMvcTest(JsonController.class)
public class RestControllerTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    SplitterService service;

    ObjectMapper mapper = new ObjectMapper();


    @Test
    @DisplayName("Gruppe erstellen klappt mit gültiger Gruppe")
    public void testAddGruppe() throws Exception {

        String [] personen = {"bernd", "hans"};
        GruppeFuerJson gruppe = new GruppeFuerJson("g", personen);


        int mockId = 0;
        when(service.createGruppeForJson(gruppe.name(), Set.of(gruppe.personen())))
                .thenReturn(mockId);


        MvcResult result = mvc.perform(post("/api/gruppen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gruppe)))
                .andExpect(status().isCreated())
                .andReturn();


        int responseId = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Integer.class);
        assertThat(mockId).isEqualTo(responseId);
    }


    @Test
    @DisplayName("Gruppen von eingeloggten User anzeigen klappt mit gültigem Login")
    @WithMockOAuth2User(login = "bernd")
    public void testGetGruppen() throws Exception {

        Gruppe g1 = new Gruppe("g1", Set.of("bernd", "hans"), 0);
        Gruppe g2 = new Gruppe("g2", Set.of("ahemyu" , "bernd"), 1);
        List<Gruppe> gruppen = List.of(g1, g2);
        when(service.findAlleGruppenVon("bernd")).thenReturn(gruppen);

        JSONObject [] gruppenNamen = new JSONObject[gruppen.size()];

        JsonValidation.createJsonGruppe(gruppen, gruppenNamen);
        String gruppenNamenString = mapper.writeValueAsString(gruppenNamen);

        MvcResult result = mvc.perform(get("/api/user/{login}/gruppen", "bernd"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(gruppenNamenString);
        verify(service).findAlleGruppenVon("bernd");

    }


    @Test
    @DisplayName("Gruppe anzeigen mit gültiger + vorhandener ID klappt")
    void  getGruppe() throws Exception {

        Gruppe g = new Gruppe("g1", Set.of("bernd", "hans"), 0);
        g.addAusgabe("bernd", Set.of("bernd", "hans"), Eur.of(100), "essen");
        g.addAusgabe("hans", Set.of("bernd", "hans"), Eur.of(50), "trinken");
        when(service.getGruppe(g.getId())).thenReturn(Optional.of(g));
        JSONObject gruppeJson =  JsonValidation.getJsonObjectForGruppe(g);

        MvcResult result = mvc.perform(get("/api/gruppen/{id}", 0))
                .andExpect(status().isOk())
                .andReturn();

        String gruppeString = mapper.writeValueAsString(gruppeJson);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(gruppeString);

    }


    @Test
    @DisplayName("Gruppe schließen mit gültiger GruppenID klappt")
    void postGruppeSchließen() throws Exception {

        Gruppe g = new Gruppe("g1", Set.of("bernd", "hans"), 0);
        when(service.getGruppe(g.getId())).thenReturn(Optional.of(g));

        mvc.perform(post("/api/gruppen/{id}/schliessen", 0))
                .andExpect(status().isOk());

        verify(service).gruppeSchliessen(0);
    }


    @Test
    @DisplayName("Auslage hinzufügen mit gültiger GruppenID klappt")
    void postAuslage() throws Exception {

        String [] personen = {"bernd", "hans"};
        int gruppenId= 0;
        double betrag = 10000;
        Gruppe g = new Gruppe("g1", Set.of("bernd", "hans"), gruppenId);
        when(service.getGruppe(g.getId())).thenReturn(Optional.of(g));
        AuslageFuerJson auslage = new AuslageFuerJson("Essen", "bernd", String.valueOf(betrag), personen);

        MvcResult result =  mvc.perform(post("/api/gruppen/{id}/auslagen", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(auslage)))
                .andExpect(status().isCreated())
                .andReturn();

        verify(service).addAusgabe(auslage.glaeubiger(), Set.of(auslage.schuldner()), Eur.of(betrag),auslage.grund(), gruppenId) ;
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEmpty();

    }


    @Test
    @DisplayName("Ausgleich anzeigen klappt mit gültiger GruppenID")
    void getAusgleich() throws Exception {

        Gruppe g = new Gruppe("g1", Set.of("bernd", "hans", "lukas"), 0);
        g.addAusgabe("bernd", Set.of("bernd", "hans", "lukas"), Eur.of(100), "essen");
        g.addAusgabe("hans", Set.of("bernd", "hans", "lukas"), Eur.of(50), "trinken");
        g.addAusgabe("lukas", Set.of("bernd", "hans", "lukas"), Eur.of(25), "kippen");
        when(service.getGruppe(g.getId())).thenReturn(Optional.of(g));

        JSONArray gesamtesObjekt = new JSONArray();
        JsonValidation.createBalanceJsonArray(g, gesamtesObjekt);

        MvcResult result = mvc.perform(get("/api/gruppen/{id}/ausgleich", 0))
                .andExpect(status().isOk())
                .andReturn();

        String ausgabe = mapper.writeValueAsString(gesamtesObjekt);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(ausgabe);

    }

}


