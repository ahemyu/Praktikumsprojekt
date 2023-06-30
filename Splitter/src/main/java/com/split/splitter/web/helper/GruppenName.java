package com.split.splitter.web.helper;

import javax.validation.constraints.NotBlank;

public class GruppenName {
  @NotBlank(message = "Grupenname darf nicht leer sein") String gruppenName;

  public String getGruppenName() {
    return gruppenName;
  }

  public void setGruppenName(String gruppenName) {
    this.gruppenName = gruppenName;
  }
}
