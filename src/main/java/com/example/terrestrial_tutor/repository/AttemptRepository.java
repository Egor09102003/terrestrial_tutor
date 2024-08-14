package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Репозиторий сущности попытки
 */
public interface AttemptRepository extends JpaRepository<AttemptEntity, Long> {
    /**
     * Поиск попытки по ученику, дз и номеру попытки
     *
     * @param pupil    ученик
     * @param homework дз
     * @param number   номер попытки
     * @return сущность попытки
     */
    AttemptEntity findAttemptEntityByPupilAndHomeworkAndAttemptNumber(PupilEntity pupil, HomeworkEntity homework, int number);
}
