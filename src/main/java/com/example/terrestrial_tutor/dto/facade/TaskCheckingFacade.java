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
public class TaskCheckingFacade {

    @NonNull
    TaskFacade taskFacade;
    @NonNull
    HomeworkService homeworkService;
    @NonNull
    TaskCheckingService taskCheckingService;
    @NonNull
    TaskService taskService;

    public TaskCheckingDTO taskCheckingToTaskCheckingDTO (TaskCheckingEntity taskChecking) {
        TaskCheckingDTO taskCheckingDTO = new TaskCheckingDTO();
        taskCheckingDTO.setCheckingType(taskChecking.getCheckingType());
        taskCheckingDTO.setId(taskChecking.getId());
        taskCheckingDTO.setHomework(taskChecking.getHomework().getId());
        taskCheckingDTO.setTask(taskFacade.taskToTaskDTO(taskChecking.getTask()));
        return taskCheckingDTO;
    }

    public TaskCheckingEntity taskCheckingDTOToTaskChecking(TaskCheckingDTO taskCheckingDTO) {
        TaskCheckingEntity taskCheckinging = new TaskCheckingEntity();
        taskCheckinging.setId(taskCheckingDTO.getId());
        taskCheckinging.setCheckingType(taskCheckingDTO.getCheckingType());
        taskCheckinging.setHomework(homeworkService.getHomeworkById(taskCheckingDTO.getHomework()));
        taskCheckinging.setTask(taskService.getTaskById(taskCheckingDTO.getTask().getId()));
        return taskCheckinging;
    }
    
}
