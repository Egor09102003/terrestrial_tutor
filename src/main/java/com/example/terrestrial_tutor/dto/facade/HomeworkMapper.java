package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.dto.TaskCheckingDTO;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.service.*;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Класс для перевода HomeworkEntity в HomeworkDTO и обратно
 */

@Component
@AllArgsConstructor
public class HomeworkMapper {

    @NonNull
    SubjectService subjectService;
    @NonNull
    PupilService pupilService;
    @NonNull
    TaskCheckingMapper taskCheckingMapper;
    @NonNull
    HomeworkService homeworkService;

    /**
     * Метод для перевода сущности дз в DTO
     *
     * @param homework домашнее задание
     * @return дз DTO
     */

    public HomeworkDTO homeworkToHomeworkDTO(HomeworkEntity homework, boolean withAnswers) {
        HomeworkDTO homeworkDTO = new HomeworkDTO();
        homeworkDTO.setId(homework.getId());
        homeworkDTO.setSubject(homework.getSubject().getName());
        homeworkDTO.setName(homework.getName());
        homeworkDTO.setDeadLine(homework.getDeadLine());
        homeworkDTO.setPupilIds(homework.getPupils().
                stream().
                map(PupilEntity::getId).
                toList());
        homeworkDTO.setTargetTime(homework.getTargetTime());
        LinkedHashMap<Long, TaskCheckingDTO> taskCheckingDTOMap = new LinkedHashMap<>();
        for (Map.Entry<Long, TaskCheckingEntity> entry : homework.getTaskCheckingTypes().entrySet()) {
            TaskCheckingDTO taskCheckingDTO = taskCheckingMapper.toTaskCheckingDTO(entry.getValue(), withAnswers);
            taskCheckingDTOMap.put(entry.getKey(), taskCheckingDTO);
        }
        homeworkDTO.setTaskIds(new LinkedList<>(homework.getTaskCheckingTypes().keySet()));
        homeworkDTO.setTaskChecking(taskCheckingDTOMap);
        homeworkDTO.setCreatedAt(homework.getCreatedAt());
        return homeworkDTO;
    }
}
