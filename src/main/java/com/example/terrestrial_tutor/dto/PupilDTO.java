package com.example.terrestrial_tutor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс DTO ученика
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PupilDTO {
    Long id;
    Double balance;
    Integer lessonPrice;
    List<String> subjects;
    List<Long> tutors;
    String username;
    String name;
    String surname;
    String patronymic;
    AttemptDTO lastAttempt;
}
