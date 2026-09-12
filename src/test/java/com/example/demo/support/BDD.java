package com.example.demo.support;

import java.lang.annotation.*;
import org.junit.jupiter.api.DisplayNameGeneration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DisplayNameGeneration(BddDisplayNameGenerator.class)
public @interface BDD {}
