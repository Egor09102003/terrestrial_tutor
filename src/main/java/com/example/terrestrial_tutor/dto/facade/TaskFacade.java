package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.entity.User;
import com.example.terrestrial_tutor.entity.enums.AnswerTypes;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.service.SubjectService;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Класс для перевода сущности задания в сущность DTO
 */
@Component
@AllArgsConstructor
public class TaskFacade {

    @NonNull
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() == ERole.PUPIL) {
            answers = null;
        }
        TaskDTO taskDTO = new TaskDTO(
                task.getId(),
                task.getName(),
                task.getChecking(),
                task.getAnswerType().name(),
                task.getTaskText(),
                answers,
                task.getSubject().getName(),
                task.getLevel1(),
                task.getLevel2(),
                task.getTable(),
                task.getAnalysis(),
                task.getCost(), 
                null);
        taskDTO.setFiles(task.getFiles());
        return taskDTO;
    }

    public TaskEntity taskDTOToTask(TaskDTO taskDTO, SupportEntity support) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskDTO.getId());
        taskEntity.setName(taskDTO.getName());
        taskEntity.setChecking(taskDTO.getChecking());
        taskEntity.setAnswerType(AnswerTypes.valueOf(taskDTO.getAnswerType()));
        taskEntity.setTaskText(taskDTO.getTaskText());
        taskEntity.setFiles(taskDTO.getFiles());
        taskEntity.setId(taskDTO.getId());
        taskEntity.setLevel1(taskDTO.getLevel1());
        taskEntity.setLevel2(taskDTO.getLevel2());
        taskEntity.setTable(taskDTO.getTable());
        taskEntity.setSubject(subjectService.findSubjectByName(taskDTO.getSubject()));
        taskEntity.setAnswer(new Gson().toJson(taskDTO.getAnswers()));
        taskEntity.setSupport(support);
        taskEntity.setAnalysis(taskDTO.getAnalysis());
        taskEntity.setCost(taskDTO.getCost());
        return taskEntity;
    }
}
