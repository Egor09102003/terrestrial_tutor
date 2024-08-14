package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.PupilEntity;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Репозиторий сущности ученика
 */
public interface PupilRepository extends JpaRepository<PupilEntity, Long> {
    /**
     * Поиск ученика по id
     *
     * @param id id
     * @return ученик
     */
    PupilEntity findPupilEntityById(Long id);

    /**
     * Поиск ученика по логину
     *
     * @param username логин
     * @return ученик
     */
    PupilEntity findPupilEntityByUsername(String username);
}
