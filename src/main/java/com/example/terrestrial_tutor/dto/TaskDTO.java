package com.example.terrestrial_tutor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Класс DTO задания
 */
@Getter
@Setter
@AllArgsConstructor
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
}
