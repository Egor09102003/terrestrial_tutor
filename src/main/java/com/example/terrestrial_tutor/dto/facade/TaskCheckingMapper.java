package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.TaskCheckingDTO;
import com.example.terrestrial_tutor.entity.TaskCheckingEntity;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskCheckingMapper {

    @NonNull
    TaskMapper taskMapper;

    public TaskCheckingDTO toTaskCheckingDTO (TaskCheckingEntity taskChecking, boolean withAnswers) {
        TaskCheckingDTO taskCheckingDTO = new TaskCheckingDTO();
        taskCheckingDTO.setCheckingType(taskChecking.getCheckingType());
        taskCheckingDTO.setId(taskChecking.getId());
        taskCheckingDTO.setHomework(taskChecking.getHomework().getId());
        taskCheckingDTO.setOrderIndex(taskChecking.getOrderIndex());
        if (withAnswers) {
            taskCheckingDTO.setTask(taskMapper.taskToTaskDTO(taskChecking.getTask()));
        } else {
            taskCheckingDTO.setTask(taskMapper.taskToTaskDTOWithoutAnswers(taskChecking.getTask()));
        }
        return taskCheckingDTO;
    }
    
}
