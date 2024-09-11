package com.example.terrestrial_tutor.dto;

import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

/**
 * Класс DTO ответов на дз
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkAnswersDTO {

    HashMap<Long, Status> answersStatuses = new HashMap<>();
    Integer attemptCount = -1;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Status {
        TaskStatuses status = TaskStatuses.WRONG;
        Integer points = 0;
        String currentAnswer = "";
    }
}


