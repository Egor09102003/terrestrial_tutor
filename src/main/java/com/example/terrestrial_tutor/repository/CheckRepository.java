package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.CheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Репозиторий сущности проверки
 */
public interface CheckRepository extends JpaRepository<CheckEntity, Long> {
    /**
     * Поиск проверки по id
     *
     * @param id id
     * @return сущность проверки
     */
    CheckEntity findCheckEntityById(Long id);

}
