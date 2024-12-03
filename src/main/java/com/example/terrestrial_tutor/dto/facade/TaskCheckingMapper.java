package com.example.terrestrial_tutor.dto.facade;

import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.TaskCheckingDTO;
import com.example.terrestrial_tutor.entity.TaskCheckingEntity;
import com.example.terrestrial_tutor.service.HomeworkService;
import com.example.terrestrial_tutor.service.TaskCheckingService;
import com.example.terrestrial_tutor.service.TaskService;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Component
@AllArgsConstructor
public class TaskCheckingMapper {

    @NonNull
    TaskMapper taskMapper;
    @NonNull
    HomeworkService homeworkService;
    @NonNull
    TaskCheckingService taskCheckingService;
    @NonNull
    TaskService taskService;

    public TaskCheckingDTO toTaskCheckingDTO (TaskCheckingEntity taskChecking, boolean withAnswers) {
        TaskCheckingDTO taskCheckingDTO = new TaskCheckingDTO();
        taskCheckingDTO.setCheckingType(taskChecking.getCheckingType());
        taskCheckingDTO.setId(taskChecking.getId());
        taskCheckingDTO.setHomework(taskChecking.getHomework().getId());
        if (withAnswers) {
            taskCheckingDTO.setTask(taskMapper.taskToTaskDTO(taskChecking.getTask()));
        } else {
            taskCheckingDTO.setTask(taskMapper.taskToTaskDTOWithoutAnswers(taskChecking.getTask()));
        }
        return taskCheckingDTO;
    }
    
}
