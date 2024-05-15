package com.testlibrary.testlibrary.annotation;

import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorMethod {

    String message() default "Invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
