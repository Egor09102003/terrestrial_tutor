package com.example.terrestrial_tutor.dto;

import com.example.terrestrial_tutor.entity.enums.TaskStatuses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerDTO {

    Long id;
    String answer;
    TaskDTO task;
    Integer points = 0;
    TaskStatuses status = TaskStatuses.ON_CHECKING;
}
