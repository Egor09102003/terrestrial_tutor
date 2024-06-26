package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Класс DTO пользователя
 */
@Getter
@Setter
public class CheckDTO {
    Long id;
    String username;
    String name;
    String surname;
    String patronymic;
    Date date;
    String role;
}
