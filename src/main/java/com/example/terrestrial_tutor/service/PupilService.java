package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;

import java.util.Collection;
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
    PupilEntity createPupil(RegistrationRequest userIn);

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

    List<AttemptEntity> getAttemptsByHomework(PupilEntity pupil, HomeworkEntity homework);

    List<PupilEntity> findByTutorAndHomework(TutorEntity tutor, HomeworkEntity homework);
}
