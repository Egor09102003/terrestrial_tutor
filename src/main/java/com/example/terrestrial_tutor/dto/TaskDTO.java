package com.example.terrestrial_tutor.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Класс DTO задания
 */
@Getter
@Setter
@RequiredArgsConstructor
public class TaskDTO {
    final Long id;
    final String name;
    final Integer checking;
    final String answerType;
    final String taskText;
    final List<?> answers;
    final String subject;
    final String level1;
    final String level2;
    final String table;
    Set<String> files;
}
