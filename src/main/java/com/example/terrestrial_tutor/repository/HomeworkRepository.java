package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * Репозиторий сущности домашнего задания
 */
public interface HomeworkRepository extends JpaRepository<HomeworkEntity, Long> {
    /**
     * Поиск дз по id
     *
     * @param id id
     * @return сущность дз
     */
    HomeworkEntity findHomeworkEntityById(Long id);
//    List<HomeworkEntity> findHomeworkEntitiesByPupils(PupilEntity[] pupils);

    /**
     * Поиск дз по репетитору
     *
     * @param tutor репетитор
     * @return лист сущностей дз
     */
    List<HomeworkEntity> findHomeworkEntitiesByTutors(TutorEntity tutor);

    /**
     * Поиск дз по ученикам
     *
     * @param pupils ученики
     * @return лист сущностей дз
     */

    List<HomeworkEntity> findHomeworkEntitiesByPupilsIn(Set<PupilEntity> pupils);

    /**
     * Serch by pupil and subject
     * 
     * @param pupil
     * @param subject
     * @return
     */
    List<HomeworkEntity> findHomeworkEntitiesByPupilsAndSubject(PupilEntity pupil, SubjectEntity subject);

    /**
     * Find hw only for current pupil
     * 
     * @param id
     * @param pupil
     * @return
     */
    HomeworkEntity findHEntityByIdAndPupils(Long id, PupilEntity pupil);
}
