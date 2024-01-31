package com.example.terrestrial_tutor.annotations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Documented
@RestController
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(value = "/api")
public @interface Api {
}
