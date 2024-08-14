package com.example.terrestrial_tutor.service;


import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;

import java.util.List;

/**
 * Сервис для работы с сущностью предмета
 */
public interface SubjectService {
    /**
     * Поиск предмета по названию
     *
     * @param name название
     * @return предмет
     */
    SubjectEntity findSubjectByName(String name);

    /**
     * Поиск предмета по id
     *
     * @param subjectId id предмета
     * @return предмет
     */

    SubjectEntity findSubjectById(Long subjectId);

    /**
     * Добавление предмета по названию и кол-ву уровней тем
     *
     * @param name        название предмета
     * @param count_level кол-во уровней тем
     * @return предмет
     */
    SubjectEntity addSubject(String name, int count_level);

    /**
     * Поиск репетиторов, которые ведут данный предмет
     *
     * @param subject предмет
     * @return лист репетиторов
     */
    List<TutorEntity> findSubjectTutors(String subject);

    /**
     * Поиск всех предметов
     *
     * @return лист предметов
     */
    List<SubjectEntity> getAllSubjects();

    /**
     * Обновление данных предмета
     *
     * @param subject предмет
     * @return обновленный предмет
     */
    SubjectEntity updateSubject(SubjectEntity subject);

    /**
     * Сохранение предмета
     * 
     * @param subject
     * @return
     */
    SubjectEntity save(SubjectEntity subject);
}
