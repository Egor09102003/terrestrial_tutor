package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;

import java.security.Principal;
import java.util.List;

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

}
