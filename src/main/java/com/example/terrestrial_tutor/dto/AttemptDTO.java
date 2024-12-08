package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<Long, AnswerDTO> answers = new HashMap<>();
}
