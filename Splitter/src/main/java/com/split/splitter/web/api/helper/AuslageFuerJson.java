package com.split.splitter.web.api.helper;

public record AuslageFuerJson(

        String grund,
        String glaeubiger,
        String cent,
        String[] schuldner) {

}
