package com.example.terrestrial_tutor.dto;

import com.example.terrestrial_tutor.entity.AnswerEntity;
import com.example.terrestrial_tutor.entity.enums.TaskStatuses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {

    Long id;
    String answer;
    TaskDTO task;
    Integer points = 0;
    TaskStatuses status = TaskStatuses.ON_CHECKING;

    public AnswerDTO (AnswerEntity answerEntity) {
        id = answerEntity.getId();
        answer = answerEntity.getAnswer();
        task = new TaskDTO(answerEntity.getTask());
        task.setAnswers(null);
        points = answerEntity.getPoints();
        status = answerEntity.getStatus();
    }
}
