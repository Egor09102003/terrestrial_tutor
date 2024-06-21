package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.CheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

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
