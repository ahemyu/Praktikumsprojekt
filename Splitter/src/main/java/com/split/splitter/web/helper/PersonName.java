package com.split.splitter.web.helper;


import javax.validation.constraints.Pattern;

public class PersonName {
  @Pattern(regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9]|-(?=[a-zA-Z0-9])){0,38}$",
          message = "Personenname darf nicht leer sein") String personName;

  public String getPersonName() {
    return personName;
  }

  public void setPersonName(String personName) {
    this.personName = personName;
  }
}
