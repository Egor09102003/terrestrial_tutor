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
    Integer lessonPrice;
    List<String> subjects;
    List<Long> tutors;
    String username;
    String name;
    String surname;
    String patronymic;
    List<AttemptDTO> homeworkAttempts;
}
