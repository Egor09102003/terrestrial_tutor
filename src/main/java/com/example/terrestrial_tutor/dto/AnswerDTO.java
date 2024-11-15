package com.example.terrestrial_tutor.dto;

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
}
