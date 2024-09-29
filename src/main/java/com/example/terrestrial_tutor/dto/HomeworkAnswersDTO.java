package com.example.terrestrial_tutor.dto;

import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Класс DTO ответов на дз
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkAnswersDTO {

    LinkedHashMap<Long, Status> answersStatuses = new LinkedHashMap<>();
    Integer attemptCount = -1;
    LinkedHashSet<Long> ordering = new LinkedHashSet<>();


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Status {
        TaskStatuses status = TaskStatuses.WRONG;
        Integer points = 0;
        String currentAnswer = "";
    }
}


