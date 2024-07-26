package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.SupportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий сущности тех поддержки
 */
public interface SupportRepository extends JpaRepository<SupportEntity, Long> {
    /**
     * Поиск тех поддержки по id
     *
     * @param id id
     * @return тех поддержка
     */
    SupportEntity findSupportEntityById(Long id);

    /**
     * Поиск тех поддержки по логину
     *
     * @param username логин
     * @return тех поддержка
     */
    SupportEntity findSupportEntityByUsername(String username);
}
