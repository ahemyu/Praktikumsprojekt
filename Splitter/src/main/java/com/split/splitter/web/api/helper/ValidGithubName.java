package com.split.splitter.web.api.helper;

public class ValidGithubName {


  public static boolean isValid(String name) {
    return name.matches("^[a-zA-Z0-9](?:[a-zA-Z0-9]|-(?=[a-zA-Z0-9])){0,38}$");
  }
}
