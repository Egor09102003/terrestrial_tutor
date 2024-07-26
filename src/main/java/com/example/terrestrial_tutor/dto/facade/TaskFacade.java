package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TaskService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Класс для перевода сущности задания в сущность DTO
 */
@Component
public class TaskFacade {

    @Autowired
    TaskService taskService;
    @Autowired
    SubjectService subjectService;

    /**
     * Метод для перевода сущности задания в сущность DTO
     *
     * @param task задание
     * @return задание DTO
     */

    public TaskDTO taskToTaskDTO(TaskEntity task) {
        LinkedList<?> answers = new LinkedList<>();
        if (task.getAnswer() != null) {
            answers = new Gson().fromJson(task.getAnswer(), LinkedList.class);
        }
        TaskDTO taskDTO = new TaskDTO(
                task.getId(),
                task.getName(),
                task.getChecking(),
                task.getAnswerType(),
                task.getTaskText(),
                answers,
                task.getSubject().getName(),
                task.getLevel1(),
                task.getLevel2(),
                task.getTable());
        taskDTO.setFiles(task.getFiles());
        return taskDTO;
    }

    public TaskEntity taskDTOToTask(TaskDTO taskDTO, SupportEntity support) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(taskDTO.getName());
        taskEntity.setChecking(taskDTO.getChecking());
        taskEntity.setAnswerType(taskDTO.getAnswerType());
        taskEntity.setTaskText(taskDTO.getTaskText());
        taskEntity.setFiles(taskDTO.getFiles());
        taskEntity.setId(taskDTO.getId());
        taskEntity.setLevel1(taskDTO.getLevel1());
        taskEntity.setLevel2(taskDTO.getLevel2());
        taskEntity.setTable(taskDTO.getTable());
        taskEntity.setSubject(subjectService.findSubjectByName(taskDTO.getSubject()));
        taskEntity.setAnswer(new Gson().toJson(taskDTO.getAnswers()));
        taskEntity.setSupport(support);
        return taskEntity;
    }
}
