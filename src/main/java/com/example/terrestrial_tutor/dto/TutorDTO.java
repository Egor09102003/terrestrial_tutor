package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс DTO листа репетиторов
 */
@Getter
@Setter
public class TutorDTO {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String patronymic;
}
