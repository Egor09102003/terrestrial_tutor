package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.PupilEntity;
import java.util.Set;

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

    @Query(value = "SELECT DISTINCT on(pupils.id) * FROM pupils JOIN enrollments e on pupils.id = e.pupil AND e.tutor = ?1 AND e.subject = ?2", nativeQuery = true)
    Set<PupilEntity> findByTutorAndSubject(Long tutorId, Long subjectId);

    @Query(value = "SELECT DISTINCT on(pupils.id) * FROM pupils JOIN enrollments e on pupils.id = e.pupil AND e.tutor = ?1", nativeQuery = true)
    Set<PupilEntity> findByTutor(Long tutorId);
}
