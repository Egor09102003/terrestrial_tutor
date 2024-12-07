package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    LinkedHashMap<Long, TaskCheckingDTO> taskChecking = new LinkedHashMap<>();
    LocalDate deadLine;
    String subject;
    LocalDateTime createdAt;
}
