package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.service.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для перевода HomeworkEntity в HomeworkDTO и обратно
 */

@Component
public class HomeworkFacade {

    @Autowired
    TaskFacade taskFacade;
    @Autowired
    SubjectService subjectService;
    @Autowired
    HomeworkService homeworkService;
    @Autowired
    PupilService pupilService;
    @Autowired
    TaskService taskService;

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

        Map<Long, String> tasksCheckingTypes = new Gson().fromJson(homework.getTaskCheckingTypes(), (new TypeToken<Map<Long, String>>() {
        }.getType()));
        homeworkDTO.setTasksCheckingTypes(tasksCheckingTypes);
        List<TaskDTO> tasks = new LinkedList<>();
        for (Map.Entry<Long, String> task : tasksCheckingTypes.entrySet()) {
            homework.getSubject().getTasks().stream().filter(currentTask
                            -> Objects.equals(currentTask.getId(), task.getKey())).findFirst().
                    ifPresent(taskEntity -> {
                        taskEntity.setAnswer("");
                        tasks.add(taskFacade.taskToTaskDTO(taskEntity));
                    });
        }

        homeworkDTO.setTasks(tasks);
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
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        homework.setTutor(tutor);
        homework.setPupils(homeworkDTO.getPupilIds().stream()
                .map(id -> pupilService.findPupilById(id)).collect(Collectors.toSet()));

        if (homeworkDTO.getTasksCheckingTypes() != null) {
            LinkedHashMap<Long, String> tasksCheckingTypes = new LinkedHashMap<>();
            for (TaskDTO task : homeworkDTO.getTasks()) {
                if (homeworkDTO.getTasksCheckingTypes().containsKey(task.getId())) {
                    tasksCheckingTypes.put(task.getId(), homeworkDTO.getTasksCheckingTypes().get(task.getId()));
                }
            }
            homework.setTaskCheckingTypes(new Gson().toJson(tasksCheckingTypes));
        }

        return homework;
    }
}
