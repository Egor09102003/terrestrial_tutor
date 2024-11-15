package com.example.terrestrial_tutor.dto;

import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCheckingDTO {

    Long id;
    TaskDTO task;
    TaskCheckingType checkingType;
    Long homework;
}
