package com.example.terrestrial_tutor.dto.facade;

import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.AttemptDTO;
import com.example.terrestrial_tutor.entity.AttemptEntity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
@AllArgsConstructor
public class AttemptMapper {

    @NonNull
    AnswerMapper answerMapper;

    public AttemptDTO attemptToAttemptDTO(AttemptEntity attempt, boolean withTaskAnswers) {
        AttemptDTO attemptDTO = new AttemptDTO();
        attemptDTO.setAnswers(attempt.getAnswers().values().stream().map(answer -> answerMapper.answerToAnswerDTO(answer, withTaskAnswers)).toList());
        attemptDTO.setId(attempt.getId());
        attemptDTO.setAttemptNumber(attempt.getAttemptNumber());
        if (withTaskAnswers) {
            attemptDTO.setAttemptPoints(attempt.getAttemptPoints());
        } else {
            attemptDTO.setAttemptPoints(null);
        }
        attemptDTO.setHomework(attempt.getHomework().getId());
        attemptDTO.setPupil(attempt.getPupil().getId());
        attemptDTO.setSolutionDate(attempt.getSolutionDate());
        attemptDTO.setStatus(attempt.getStatus());
        return attemptDTO;
    }
}
