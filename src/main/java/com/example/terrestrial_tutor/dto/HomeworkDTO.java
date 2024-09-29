package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

/**
 * Класс DTO дз
 */
@Getter
@Setter
public class HomeworkDTO {
    Long id;
    String name;
    Long targetTime;
    List<Long> pupilIds = new ArrayList<>();
    Map<Long, String> tasksCheckingTypes;
    LocalDate deadLine;
    @NonNull
    String subject;
    LinkedList<TaskDTO> tasks = new LinkedList<>();
    Integer lastAttempt = 0;
}
