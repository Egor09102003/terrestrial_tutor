package com.example.terrestrial_tutor.payload.response;

import com.example.terrestrial_tutor.dto.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TasksResponse {
    private Long total;
    private List<TaskDTO> tasks;
}
