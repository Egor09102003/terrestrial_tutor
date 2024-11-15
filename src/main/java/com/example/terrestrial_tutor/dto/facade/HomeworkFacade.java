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
public class HomeworkFacade {

    @NonNull
    SubjectService subjectService;
    @NonNull
    PupilService pupilService;
    @NonNull
    TaskCheckingFacade taskCheckingFacade;
    @NonNull
    HomeworkService homeworkService;

    /**
     * Метод для перевода сущности дз в DTO
     *
     * @param homework домашнее задание
     * @return дз DTO
     */

    public HomeworkDTO homeworkToHomeworkDTO(HomeworkEntity homework) {
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
        homeworkDTO.setTaskChecking(new LinkedList<>(
            homework.getTaskCheckingTypes().stream()
                    .map(checking -> taskCheckingFacade.taskCheckingToTaskCheckingDTO(checking))
                    .toList()
        ));
        return homeworkDTO;
    }

    /**
     * Метод для перевода DTO дз в сущность
     *
     * @param homeworkDTO домашнее задание DTO
     * @return сущность домашнего задания
     */

    public HomeworkEntity homeworkDTOToHomework(HomeworkDTO homeworkDTO) {
        HomeworkEntity homework = new HomeworkEntity();
        homework.setId(homeworkDTO.getId());
        homework.setName(homeworkDTO.getName());
        homework.setDeadLine(homeworkDTO.getDeadLine());
        homework.setSubject(subjectService.findSubjectByName(homeworkDTO.getSubject()));
        homework.setPupils(new HashSet<>(pupilService.findPupilsByIds(homeworkDTO.getPupilIds())));
        for(TaskCheckingDTO checking : homeworkDTO.getTaskChecking()) {
            homework.getTaskCheckingTypes().add(taskCheckingFacade.taskCheckingDTOToTaskChecking(checking));
        } 

        return homework;
    }
}
