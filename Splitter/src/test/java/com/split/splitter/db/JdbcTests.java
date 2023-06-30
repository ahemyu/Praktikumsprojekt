package com.split.splitter.db;

import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import com.split.splitter.helper.parser.AusgabeAlsString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJdbcTest
@ActiveProfiles("test")
public class JdbcTests {

    @Autowired
    SpringDataSplitterRepository springDataSplitterRepository;
    //RepoImpl repoImpl;

    RepoImpl repoImpl;

  @BeforeEach
  void setUp() {
      repoImpl = new RepoImpl(springDataSplitterRepository);
  }
  @AfterEach

  void tearDown(){
      List<Gruppe> g = repoImpl.findAll();
      g.forEach(e-> System.out.println(e.getName() + " " + e.getId() + " "
              + e.istGeschlossen() + " " + e.getPersonen()));


  }

    @Test
    @DisplayName("Gruppe alle finden klappt")
    void findAllKlappt(){

      int id = repoImpl.save(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), null));
      List<Gruppe> g = repoImpl.findAll();
      g.forEach(e-> System.out.println(e.getName() + " " + e.getId() + " "
              + e.istGeschlossen() + " " + e.getPersonen()));

      assertThat(g).containsExactly(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), id));




    }

    @Test
    @DisplayName("Gruppe saven klappt")
    void saveKlappt(){


        int id = repoImpl.save(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), null));

        List<Gruppe> g = repoImpl.findAll();


        assertThat(g).containsExactly(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), id));


    }

    @Test
    @DisplayName("man kann Ausgabe hinzufügen")
    //@Sql("classpath:db/add_data.sql")
    void ausgabeHinzufügenKlappt(){


        int id = repoImpl.save(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), null));
        Gruppe gruppe = repoImpl.findById(id).orElseThrow(IllegalStateException :: new);

        gruppe.addAusgabe("Max", Set.of("Max", "Moritz", "Hans"), Eur.of(10.0), "Essen");

        int savedId = repoImpl.save(gruppe);

        gruppe = repoImpl.findById(savedId).orElseThrow(IllegalStateException :: new);

        assertThat(gruppe.getAusgabenDarstellung())
                .containsExactly(AusgabeAlsString
                        .of("Max", Set.of("Max", "Moritz", "Hans"),
                                Eur.of(10.0), "Essen"));




    }


    @Test
    @DisplayName("find alle Gruppen von Person")
    //@Sql("classpath:db/add_gruppen.sql")
    void findAlleGruppenVonKlappt () {

        int id1 = repoImpl.save(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), null));
        int id2 = repoImpl.save(new Gruppe("Gruppe2", Set.of("Max", "Moritz", "Anna"), null));
        int id3 = repoImpl.save(new Gruppe("Gruppe3", Set.of("Maxim", "Eva", "Anna"), null));

        List<Gruppe> gruppenVonMax = repoImpl.findAlleGruppenVon("Max");



        assertThat(gruppenVonMax).containsExactly(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), id1),
                new Gruppe("Gruppe2", Set.of("Max", "Moritz", "Anna"), id2));




    }
    @Test
    @DisplayName("Gruppe schließen")
    void gruppeSchließenKlappt(){

      int id = repoImpl.save(new Gruppe("Gruppe1", Set.of("Max", "Moritz", "Hans"), null));
      Gruppe gruppe = repoImpl.findById(id).orElseThrow(IllegalStateException :: new);

      repoImpl.gruppeSchliessen(gruppe);

      List<Gruppe> g = repoImpl.findAll();
      assertThat(g.get(0).istGeschlossen()).isTrue();


    }








}