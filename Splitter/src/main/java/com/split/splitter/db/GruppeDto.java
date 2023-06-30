package com.split.splitter.db;

import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "gruppe")
public class GruppeDto {
  @Id
  private final Integer id;
  private final String name;
  private final String[] personen;
  private boolean istGeschlossen;
  private final Set<AusgabeDto> alleAusgaben;


  public GruppeDto(Integer id,
                   String name,
                   String[] personen,
                   boolean istGeschlossen,
                   Set<AusgabeDto> alleAusgaben) {
    this.id = id;
    this.name = name;
    this.personen = personen;
    this.istGeschlossen = istGeschlossen;
    this.alleAusgaben = alleAusgaben;
  }


  public Integer id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String[] personen() {
    return personen;
  }

  public boolean istGeschlossen() {
    return istGeschlossen;
  }

  public void gruppeSchliessen() {
    this.istGeschlossen = true;
  }

  public Set<AusgabeDto> alleAusgaben() {
    return alleAusgaben;
  }


}
