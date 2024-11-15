package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.AdminEntity;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;

/**
 * Сервис для работы с сущностью администратора
 */
public interface AdminService {
    /**
     * Добавление администратора
     *
     * @param userIn запрос на регистрацию
     * @return администратор
     */
    AdminEntity addNewAdmin(RegistrationRequest userIn);

    /**
     * Поиск администратора по id
     *
     * @param id id
     * @return администратор
     */

    AdminEntity findAdminById(Long id);

    /**
     * Get admin by username
     * 
     * @param username
     * @return
     */
    AdminEntity findAdminByUsername(String username);
}
