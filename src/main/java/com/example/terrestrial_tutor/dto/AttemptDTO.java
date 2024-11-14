package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;

/**
 * Класс DTO задания
 */
@Getter
@Setter
public class AttemptDTO {
    PupilFacade pupilFacade;

    Long id;
    Long homework;
    Long attemptPoints;
    PupilDTO pupil;
    Integer attemptNumber;
    HomeworkStatus status;
    Long solutionDate;
    List<AnswerDTO> answers = new ArrayList<>();

    public AttemptDTO(AttemptEntity attemptEntity) {
        pupilFacade = new PupilFacade();
        id = attemptEntity.getId();
        homework = attemptEntity.getHomework().getId();
        attemptPoints = attemptEntity.getAttemptPoints();
        pupil = pupilFacade.pupilToPupilDTO(attemptEntity.getPupil());
        attemptNumber = attemptEntity.getAttemptNumber();
        status = attemptEntity.getStatus();
        solutionDate = attemptEntity.getSolutionDate();
        answers = attemptEntity.getAnswers().stream().map(answer -> new AnswerDTO(answer)).toList();
    }
}
