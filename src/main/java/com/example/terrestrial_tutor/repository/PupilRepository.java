package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


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

    /**
     * Get all pupils by ids
     * 
     * @param ids pupil ids
     * @return pupil entities
     */
    @Query(value = "SELECT * FROM pupils WHERE id IN (?1)", nativeQuery = true)
    List<PupilEntity> findByIds(Iterable<Long> ids);
}
