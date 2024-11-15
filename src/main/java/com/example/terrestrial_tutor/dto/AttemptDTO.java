package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;

/**
 * Класс DTO задания
 */
@Getter
@Setter
public class AttemptDTO {

    Long id;
    Long homework;
    Long attemptPoints;
    Long pupil;
    Integer attemptNumber;
    HomeworkStatus status;
    Long solutionDate;
    List<AnswerDTO> answers = new ArrayList<>();
}
