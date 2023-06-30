package com.split.splitter.service;

import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ServiceTests {




    SplitterRepository repository  = mock(SplitterRepository.class);

    SplitterService service = new SplitterService(repository);


    @DisplayName("Gruppe erstellen klappt")
    @Test
    void erstellenKlappt() {

        service.createGruppe("Meine Gruppe", "otto");
        verify(repository).save(new Gruppe("Meine Gruppe", Set.of("otto"), null));
    }


    @DisplayName("Gruppen Übersicht ausgeben funktioniert")
    @Test
    void anzeigenKlappt() {

        List<Gruppe> gruppen = service.alleGruppen();
        verify(repository).findAll();

    }

    @DisplayName("find alle Gruppen von Person klappt")
    @Test
    void findAlleGruppenVonKlappt(){

        service.findAlleGruppenVon("Person2");
        verify(repository).findAlleGruppenVon("Person2");

    }

    @DisplayName("Person zu Gruppe hinzufügen klappt")
    @Test
    void addPersonToGruppeKlappt() {


        Set<String> set = new HashSet<>();
        set.add("A");
        int gruppenId = service.createGruppe("Gruppe1", "A");
        when(service.getGruppe(gruppenId)).thenReturn(Optional.of(new Gruppe("Gruppe1", set, gruppenId)));

        service.addPersonToGruppe("B", gruppenId);
        verify(repository).save(new Gruppe("Gruppe1", Set.of("A", "B"), gruppenId));
    }



    @DisplayName("Ausgabe hinzufügen klappt")
    @Test
    void addAusgabeKlappt() {



        int gruppenId = service.createGruppe("Gruppe1", "A");
        Gruppe g = new Gruppe("Gruppe1", Set.of("A", "B"), gruppenId);
        g.addAusgabe("A", Set.of("A","B"), Eur.of(10), "Pizza");
        when(service.getGruppe(gruppenId)).thenReturn(Optional.of(new Gruppe("Gruppe1", Set.of("A", "B"), gruppenId)));

        service.addAusgabe("A", Set.of("A","B"), Eur.of(10), "Pizza", gruppenId);

        verify(repository).save(g);

    }


    @DisplayName("Gruppe schließen klappt")
    @Test
    void gruppeSchließenKlappt() {

        Gruppe g = new Gruppe("Gruppe1", Set.of("A"), 0);
        when(repository.findById(0)).thenReturn(Optional.of(g));
        service.gruppeSchliessen(0);
        verify(repository).gruppeSchliessen(g);
    }


    @DisplayName("createGruppeForJson klappt")
    @Test
    void erstellenKlapptJson() {

        service.createGruppeForJson("Meine Gruppe", Set.of("otto"));
        verify(repository).save(new Gruppe("Meine Gruppe", Set.of("otto"), null));
    }




}
