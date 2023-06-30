package com.split.splitter.service;

import com.split.splitter.domain.model.Gruppe;
import java.util.List;
import java.util.Optional;


public interface SplitterRepository {

  public List<Gruppe> findAll();

  public int save(Gruppe gruppe);

  public Optional<Gruppe> findById(int id);

  public List<Gruppe> findAlleGruppenVon(String personenName);

  public void gruppeSchliessen(Gruppe gruppe);

}
