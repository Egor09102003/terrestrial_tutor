package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Сервис по работе с сущностью задания
 */
public interface TaskService {
    /**
     * Поиск заданий по предмету и теме верхнего уровня
     *
     * @param subject предмет
     * @param level1  тема верхнего уровня
     * @return лист заданий
     */
    List<TaskEntity> getTasksBySubjectAndLevel1(SubjectEntity subject, String level1);

    /**
     * Поиск заданий по предмету
     *
     * @param subject предмет
     * @return лист заданий
     */
    List<TaskEntity> getTasksBySubject(SubjectEntity subject);

    /**
     * Поиск заданий по предмету и теме нижнего уровня
     *
     * @param subject предмет
     * @param level1  тема верхнего уровня
     * @param level2  тема нижнего уровня
     * @return лист заданий
     */
    List<TaskEntity> getTasksBySubjectAndLevel2(SubjectEntity subject, String level1, String level2);

    /**
     * Поиск всех заданий
     *
     * @return лист заданий
     */
    List<TaskEntity> getAllTasks();

    /**
     * Поиск задания по id
     *
     * @param id id
     * @return задание
     */
    TaskEntity getTaskById(Long id);

    /**
     * Сохранение задания
     *
     * @param task задание
     * @return сохраненное задание
     */
    TaskEntity save(TaskEntity task);

    /**
     * Поиск заданий по фильтрации
     *
     * @param choices фильтрация
     * @param subject предмет
     * @return лист заданий
     */
    List<TaskEntity> getSelectionTask(Map<String, Integer> choices, SubjectEntity subject);

    /**
     * Добавление задания
     *
     * @param dto     задание dto
     * @param support тех поддержка, которая добавила задание
     * @return добавленное задание
     */
    TaskEntity addNewTask(TaskDTO dto, SupportEntity support);

    /**
     * Удаление задания по id
     * 
     * @param id
     * @return
     */
    Long delete(Long id);

    /**
     * Get all tasks by ids
     * 
     * @param taskIds task ids
     * @return task entities
     */
    List<TaskEntity> getByIds(Iterable<Long> taskIds);
}
