package com.example.terrestrial_tutor.payload.request;

import com.example.terrestrial_tutor.dto.TaskCheckingDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class HomeworkSaveRequest {
    Long id;
    String name;
    Long targetTime;
    List<Long> pupilIds = new ArrayList<>();
    LinkedHashMap<Long, TaskCheckingDTO> taskChecking = new LinkedHashMap<>();
    LocalDate deadLine;
    String subject;
}
