package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;

import java.util.List;

/**
 * Сервис по работе с сущностью репетитора
 */
public interface TutorService {
    /**
     * Добавление нового репетитора
     *
     * @param userIn запрос на регистрацию
     * @return репетитор
     */
    TutorEntity addNewTutor(RegistrationRequest userIn);

    /**
     * Поиск репетитора по id
     *
     * @param id id
     * @return репетитор
     */
    TutorEntity findTutorById(Long id);

    /**
     * Поиск репетиторов по предмету
     *
     * @param subject предмет
     * @return лист репетиторов
     */
    List<TutorEntity> findTutorsWithoutSubject(String subject);

    /**
     * Верификация репетитора
     *
     * @param id id
     * @return репетитор
     */
    TutorEntity verifyTutor(Long id);

    /**
     * Удаление репетитора по id
     *
     * @param id id
     */
    void deleteTutorById(Long id);

    /**
     * Поиск предметов репетитора по его id
     *
     * @param id id
     * @return лист предметов
     */
    List<SubjectEntity> findTutorSubjectsByTutorId(Long id);

    /**
     * Обновление данных репетитора
     *
     * @param tutor репетитор
     * @return обновленный репетитор
     */
    TutorEntity updateTutor(TutorEntity tutor);

    /**
     * Добавление предмета репетитору
     *
     * @param tutor   репетитор
     * @param subject предмет
     * @return репетитор
     */
    TutorEntity addTutorSubject(TutorEntity tutor, SubjectEntity subject);
}
