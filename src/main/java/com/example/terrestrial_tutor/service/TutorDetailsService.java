package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.entity.User;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Сервис авторизации репетитора
 */
public interface TutorDetailsService extends UserDetailsService {
    /**
     * Загрузка пользователя по логину
     *
     * @param username логин
     * @return пользователь
     */
    User loadUserByUsername(String username);

    /**
     * Поиск репетитора по id
     *
     * @param id id
     * @return репетитор
     */
    TutorEntity loadTutorById(Long id);
}
