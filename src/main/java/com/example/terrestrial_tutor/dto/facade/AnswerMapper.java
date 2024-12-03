package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.AnswerDTO;
import com.example.terrestrial_tutor.entity.AnswerEntity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
@AllArgsConstructor
public class AnswerMapper {

    @NonNull
    TaskMapper taskMapper;

    public AnswerDTO answerToAnswerDTO(AnswerEntity answer, boolean withAnswers) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setId(answer.getId());
        answerDTO.setAnswer(answer.getAnswer());
        if (withAnswers) {
            answerDTO.setTask(taskMapper.taskToTaskDTO(answer.getTask()));
            answerDTO.setPoints(answer.getPoints());
            answerDTO.setStatus(answer.getStatus());
        } else {
            answerDTO.setTask(taskMapper.taskToTaskDTOWithoutAnswers(answer.getTask()));
            if (answer.getAttempt().getHomework().getTaskCheckingTypes().get(answer.getTask().getId()).getCheckingType() != TaskCheckingType.INSTANCE) {
                answerDTO.setStatus(null);
                answerDTO.setPoints(null);
            } else {
                answerDTO.setPoints(answer.getPoints());
                answerDTO.setStatus(answer.getStatus());
            }
        }
        return answerDTO;
    }
}
