package com.example.terrestrial_tutor.dto.facade;

import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.AnswerDTO;
import com.example.terrestrial_tutor.entity.AnswerEntity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
@AllArgsConstructor
public class AnswerFacade {
    
    @NonNull
    TaskFacade taskFacade;

    public AnswerDTO answerToAnswerDTO(AnswerEntity answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setId(answer.getId());
        answerDTO.setAnswer(answer.getAnswer());
        answerDTO.setTask(taskFacade.taskToTaskDTO(answer.getTask()));
        answerDTO.setPoints(answer.getPoints());
        answerDTO.setStatus(answer.getStatus());
        return answerDTO;
    }
}
