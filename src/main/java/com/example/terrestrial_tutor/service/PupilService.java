package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с сущностью ученика
 */
public interface PupilService {
    /**
     * Добавление нового ученика
     *
     * @param userIn запрос на регистрацию
     * @return ученик
     */
    PupilEntity addNewPupil(RegistrationRequest userIn);

    /**
     * Поиск ученика по id
     *
     * @param id id
     * @return ученика
     */

    PupilEntity findPupilById(Long id);

    /**
     * Поиск учеников по id
     *
     * @param ids id учеников
     * @return лист учеников
     */
    List<PupilEntity> findPupilsByIds(List<Long> ids);

    /**
     * Верификация ученика
     *
     * @param id id ученика
     * @return ученик
     */
    PupilEntity verifyPupil(Long id);

    /**
     * Поиск текущего авторизованного ученика
     *
     * @param principal авторизированный пользователь
     * @return ученик
     */

    PupilEntity getCurrentPupil(Principal principal);

    /**
     * Обновление данных ученика
     *
     * @param pupil ученик
     * @return обновленный ученик
     */
    PupilEntity updatePupil(PupilEntity pupil);

    /**
     * Поиск всех учеников
     *
     * @return лист учеников
     */

    List<PupilEntity> findAllPupils();

    /**
     * Get pupil by ids
     * 
     * @param ids pupil ids
     * @return pupil entities
     */
    List<PupilEntity> getByIds(Iterable<Long> ids);

    /**
     * Get all tutor pupils
     * 
     * @param tutorId tutor id
     * @return set of pupil entities
     */
    Set<PupilEntity> getByTutor(Long tutorId);

    /**
     * Get all tutor pupils by subject
     * 
     * @param tutorId tutor id
     * @param subjectId subjectId
     * @return set of pupil entities
     */
    Set<PupilEntity> getByTutorAndSubject(Long tutorId, Long subjectId);
}
