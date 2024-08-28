package com.example.terrestrial_tutor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс DTO ответов на дз
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkAnswersDTO {

    HashMap<Long, Status> answersStatuses = new HashMap<>();
    Integer attemptCount;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Status {
        Boolean status;
        String currentAnswer;        
    }
}


