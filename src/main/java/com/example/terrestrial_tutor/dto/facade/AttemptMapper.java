package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.AnswerDTO;
import com.example.terrestrial_tutor.entity.AnswerEntity;
import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.AttemptDTO;
import com.example.terrestrial_tutor.entity.AttemptEntity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
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
        Map<Long, AnswerDTO> answers = new HashMap<>();
        for (Map.Entry<Long, AnswerEntity> entry : attempt.getAnswers().entrySet()) {
            answers.put(entry.getKey(), answerMapper.answerToAnswerDTO(entry.getValue(), withTaskAnswers));
        }
        attemptDTO.setAnswers(answers);
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
