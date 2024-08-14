package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.User;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Сервис для авторизации тех поддержки
 */
public interface SupportDetailsService extends UserDetailsService {
    /**
     * Загрузка пользователя по логину
     *
     * @param username логин
     * @return пользователь
     */
    User loadUserByUsername(String username);

    /**
     * Поиск тех поддержки по id
     *
     * @param id id
     * @return тех поддержка
     */
    SupportEntity loadSupportById(Long id);
}
