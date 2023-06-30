package com.split.splitter.db;

import java.util.List;
import java.util.Optional;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface SpringDataSplitterRepository  extends CrudRepository<GruppeDto, Integer> {

  public List<GruppeDto> findAll();

  @Lock(LockMode.PESSIMISTIC_WRITE)
  public Optional<GruppeDto> findById(int id);



}
