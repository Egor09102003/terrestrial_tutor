package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.AdminEntity;
import com.example.terrestrial_tutor.entity.User;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Сервис авторизации администратора
 */
public interface AdminDetailsService extends UserDetailsService {
    /**
     * Загрузка пользователя по логину
     *
     * @param username логин
     * @return пользователь
     */
    User loadUserByUsername(String username);

    /**
     * Загрузка администратора по id
     *
     * @param id id
     * @return администратор
     */

    AdminEntity loadAdminById(Long id);
}
