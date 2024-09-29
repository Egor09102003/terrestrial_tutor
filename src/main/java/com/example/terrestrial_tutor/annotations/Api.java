package com.example.terrestrial_tutor.annotations;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Documented
@RestController
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(value = "/api")
@CrossOrigin(origins = {"http://localhost",
        "http://87.249.49.62",
        "http://10.0.0.2",
        "http://localhost:81",
        "http://terrestrial-tutor-dev.work.gd",
        "http://terrestrial-tutor.work.gd"
})
public @interface Api {
}
