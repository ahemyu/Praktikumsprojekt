package com.split.splitter.service;


import com.split.splitter.domain.model.Gruppe;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SplitterService {

  private final SplitterRepository repository;

  public SplitterService(SplitterRepository repository) {
    this.repository = repository;
  }


  public int createGruppe(String gruppenName, String personenName) {
    Gruppe gruppe = new Gruppe(gruppenName, null);
    gruppe.addPersonInGruppe(personenName);
    return repository.save(gruppe);
  }

  public int createGruppeForJson(String gruppenName, Set<String> personen) {

    Gruppe gruppe = new Gruppe(gruppenName, personen, null);
    return repository.save(gruppe);
  }

  public List<Gruppe> alleGruppen() {
    return repository.findAll();
  }

  public List<Gruppe> findAlleGruppenVon(String personenName) {
    return repository.findAlleGruppenVon(personenName);
  }

  //in Doku erwähnen
  @Transactional
  public Optional<Gruppe> getGruppe(int id) {
    return repository.findById(id);
  }


  @Transactional
  public void addPersonToGruppe(String personenName, int id) {
    Optional<Gruppe> gruppe = repository.findById(id);
    gruppe.ifPresent(g -> {
      g.addPersonInGruppe(personenName);
      repository.save(g);
    });
  }

  @Transactional
  public void addAusgabe(String person,
                         Set<String> teilnehmende,
                         Money betrag,
                         String beschreibung,
                         int id) {
    Optional<Gruppe> gruppe = repository.findById(id);

    gruppe.ifPresent(g -> {
      g.addAusgabe(person, teilnehmende, betrag, beschreibung);
      repository.save(g);
    });
  }

  @Transactional
  public void gruppeSchliessen(int id) {
    Optional<Gruppe> gruppe = repository.findById(id);
    gruppe.ifPresent(repository::gruppeSchliessen);

  }


}
