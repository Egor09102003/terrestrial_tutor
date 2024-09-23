package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий сущности предмета
 */
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    /**
     * Поиск предмета по id
     *
     * @param id id
     * @return учебный предмет
     */
    SubjectEntity findSubjectEntityById(Long id);

    /**
     * Поиск предмета по названию
     *
     * @param name название предмета
     * @return предмет
     */

    SubjectEntity findSubjectEntityByName(String name);
}
