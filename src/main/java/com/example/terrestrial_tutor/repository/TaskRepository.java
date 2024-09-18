package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Репозиторий сущности задания
 */
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    /**
     * Поиск задания по id
     *
     * @param id id
     * @return задание
     */
    TaskEntity findTaskEntityById(Long id);

    /**
     * Поиск заданий по предмету и теме верхнего уровня
     *
     * @param subject предмет
     * @param level1  тема верхнего уровня
     * @return лист заданий
     */
    List<TaskEntity> findTaskEntitiesBySubjectAndLevel1(SubjectEntity subject, String level1);

    /**
     * Поиск заданий по предмету
     *
     * @param subject предмет
     * @return лист заданий
     */
    List<TaskEntity> findTaskEntitiesBySubject(SubjectEntity subject);

    /**
     * Поиск заданий по предмету и теме нижнего уровня
     *
     * @param subject предмет
     * @param level2  тема нижнего уровня
     * @return лист заданий
     */
    List<TaskEntity> findTaskEntitiesBySubjectAndLevel2(SubjectEntity subject, String level2);

    /**
     * Поиск заданий по предмету, темам верхнего и нижнего уровня
     *
     * @param subject предмет
     * @param level1  тема верхнего уровня
     * @param level2  тема нижнего уровня
     * @return листа заданий
     */
    List<TaskEntity> findTaskEntitiesBySubjectAndLevel1AndLevel2(SubjectEntity subject, String level1, String level2);

    /**
     * Find tasks by ids
     * 
     * @param taskIds task ids
     * @return task entities
     */
    List<TaskEntity> findAllById(Iterable<Long> taskIds);
}
