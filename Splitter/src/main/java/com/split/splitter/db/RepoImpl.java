package com.split.splitter.db;

import com.split.splitter.domain.model.Gruppe;
import com.split.splitter.helper.Eur;
import com.split.splitter.service.SplitterRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;


@Repository
public class RepoImpl implements SplitterRepository {

  private final SpringDataSplitterRepository repository;

  public RepoImpl(SpringDataSplitterRepository repository) {
    this.repository = repository;
  }


  @Override
  public List<Gruppe> findAll() {

    List<GruppeDto> alleGruppen = repository.findAll();

    return alleGruppen.stream().map(this::toGruppe).collect(Collectors.toList());


  }


  @Override
  public int save(Gruppe gruppe) {

    GruppeDto dto = fromGruppe(gruppe);
    GruppeDto saved = repository.save(dto);

    return toGruppe(saved).getId();

  }

  private GruppeDto fromGruppe(Gruppe gruppe) {

    String name = gruppe.getName();

    String[] personen = new String[gruppe.getPersonen().size()];

    List<String> personenAlsList = gruppe.getPersonen().stream().toList();

    for (int i = 0; i < personen.length; i++) {
      personen[i] = personenAlsList.get(i);
    }

    boolean istGeschlossen = gruppe.istGeschlossen();

    Set<AusgabeDto> alleAusgaben = new HashSet<>();

    addAusgaben(gruppe, alleAusgaben);


    if (gruppe.getId() == null) {
      return new GruppeDto(null, name, personen, istGeschlossen, alleAusgaben);
    }

    return new GruppeDto(gruppe.getId(), name, personen, istGeschlossen, alleAusgaben);

  }

  //Hilfsmethode für fromGruppe
  private void addAusgaben(Gruppe gruppe, Set<AusgabeDto> alleAusgaben) {
    gruppe.getAusgabenDarstellung().forEach(e -> alleAusgaben
            .add(new AusgabeDto(e.zahlendePerson(),
                    e.getTeilnehmende(),
                    Double.parseDouble(e.betrag().replace('€', ' ').trim()),
                    e.beschreibung())));
  }


  private Gruppe toGruppe(GruppeDto gruppeDto) {

    int id = gruppeDto.id();
    String name = gruppeDto.name();
    Set<String> personen = Set.of(gruppeDto.personen());
    boolean istGeschlossen = gruppeDto.istGeschlossen();

    Gruppe g = new Gruppe(name, id);
    personen.forEach(e -> g.addPersonInGruppe(e));


    gruppeDto.alleAusgaben().forEach(
            e -> g.addAusgabe(
                    e.zahlendePerson(),
                    Set.of(e.schuldner()),
                    Eur.of(e.betrag()),
                    e.beschreibung()));


    if (istGeschlossen) {
      g.gruppeSchliessen();
    }

    return g;


  }

  @Override
  public void gruppeSchliessen(Gruppe gruppe) {

    GruppeDto dto = fromGruppe(gruppe);
    dto.gruppeSchliessen();
    repository.save(dto);
  }


  @Override
  public Optional<Gruppe> findById(int id) {

    GruppeDto g = repository.findById(id).orElseGet(() -> null);


    if (g == null) {

      return Optional.empty();
    }


    return Optional.of(toGruppe(g));


  }

  @Override
  public List<Gruppe> findAlleGruppenVon(String personenName) {

    List<GruppeDto> alleGruppen = repository.findAll();
    List<GruppeDto> neu = alleGruppen.stream()
            .filter(e -> Arrays.asList(
                    e.personen()).contains(personenName)).collect(Collectors.toList());


    return neu.stream().map(this::toGruppe).collect(Collectors.toList());

  }

}
