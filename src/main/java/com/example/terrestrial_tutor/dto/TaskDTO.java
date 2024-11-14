package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.example.terrestrial_tutor.entity.TaskEntity;
import com.google.gson.Gson;

/**
 * Класс DTO задания
 */
@Getter
@Setter
public class TaskDTO {
    Long id;
    String name;
    Integer checking;
    String answerType;
    String taskText;
    List<?> answers;
    String subject;
    String level1;
    String level2;
    String table;
    String analysis;
    Integer cost;
    Set<String> files;

    public TaskDTO (TaskEntity task) {
        LinkedList<?> answers = new LinkedList<>();
        if (task.getAnswer() != null) {
            answers = new Gson().fromJson(task.getAnswer(), LinkedList.class);
        }
        id = task.getId();
        name = task.getName();
        checking = task.getChecking();
        answerType = task.getAnswerType().name();
        taskText = task.getTaskText();
        this.answers = answers;
        subject = task.getSubject().getName();
        level1 = task.getLevel1();
        level2 = task.getLevel2();
        table = task.getTable();
        analysis = task.getAnalysis();
        cost = task.getCost();
        files = task.getFiles();
    }
}
