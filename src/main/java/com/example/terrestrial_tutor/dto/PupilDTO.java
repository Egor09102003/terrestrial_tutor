package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс DTO ученика
 */
@Getter
@Setter
public class PupilDTO {
    Long id;
    Double balance;
    Integer price;
    List<String> subjects;
    List<String> tutors;
    String username;
    String name;
    String surname;
    String patronymic;
    HomeworkAnswersDTO attempt;
    Integer lastAttemptNumber;
}
