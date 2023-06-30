package com.split.splitter.web.helper.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.constraints.Pattern;

@Pattern(regexp = "^[0-9]+,?([0-9]{0,2})?$", message = "invalid money format")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMoney {

}



