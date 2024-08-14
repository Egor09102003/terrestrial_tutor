package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;

import java.security.Principal;
import java.util.List;

/**
 * Сервис для работы с сущностью тех поддержки
 */
public interface SupportService {
    /**
     * Добавление тех поддержки
     *
     * @param userIn запрос на регистрацию
     * @return тех поддержка
     */
    SupportEntity addNewSupport(RegistrationRequest userIn);

    /**
     * Поиск тех поддержки по id
     *
     * @param id id
     * @return тех поддержка
     */

    SupportEntity findSupportById(Long id);

    /**
     * Поиск тех поддержки по id
     *
     * @param ids id тех поодержки
     * @return лист тех поодержки
     */
    List<SupportEntity> findSupportsByIds(List<Long> ids);

    /**
     * Поиск авторизированной тех поддержки
     *
     * @param principal авторизированный пользователь
     * @return тех поддержка
     */
    SupportEntity getCurrentSupport(Principal principal);

    /**
     * Обновление данных тех поддержки
     *
     * @param support тех поддержка
     * @return обновленная тех поддержка
     */

    SupportEntity updateSupport(SupportEntity support);

    /**
     * Поиск всех аккаунтов тех поддержки
     *
     * @return лист тех поддержки
     */

    List<SupportEntity> findAllSupports();

    /**
     * Сохранение пользователя тех поддержки
     * 
     * @param support
     * @return
     */
    SupportEntity save(SupportEntity support);
}
