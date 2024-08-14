package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.User;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Сервис по работе с профилем ученика
 */
public interface PupilDetailsService extends UserDetailsService {
    /**
     * Загрузка пользователя по логину
     *
     * @param username логин
     * @return userDetails
     */
    User loadUserByUsername(String username);

    /**
     * Загрузка ученика по id
     *
     * @param id id
     * @return ученика
     */

    PupilEntity loadPupilById(Long id);
}
