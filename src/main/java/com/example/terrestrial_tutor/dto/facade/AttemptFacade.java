package com.example.terrestrial_tutor.dto.facade;

import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.AttemptDTO;
import com.example.terrestrial_tutor.entity.AttemptEntity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
@AllArgsConstructor
public class AttemptFacade {

    @NonNull
    AnswerFacade answerFacade;

    public AttemptDTO attemptToAttemptDTO(AttemptEntity attempt) {
        AttemptDTO attemptDTO = new AttemptDTO();
        attemptDTO.setAnswers(attempt.getAnswers().stream().map(answerFacade::answerToAnswerDTO).toList());
        attemptDTO.setId(attempt.getId());
        attemptDTO.setAttemptNumber(attempt.getAttemptNumber());
        attemptDTO.setAttemptPoints(attempt.getAttemptPoints());
        attemptDTO.setHomework(attempt.getHomework().getId());
        attemptDTO.setPupil(attempt.getPupil().getId());
        attemptDTO.setSolutionDate(attempt.getSolutionDate());
        attemptDTO.setStatus(attempt.getStatus());
        return attemptDTO;
    }
}
