package com.example.terrestrial_tutor.dto;

import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import lombok.Getter;
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
    LinkedHashMap<Long, TaskCheckingDTO> taskChecking = new LinkedHashMap<>();
    LocalDate deadLine;
    String subject;
}
