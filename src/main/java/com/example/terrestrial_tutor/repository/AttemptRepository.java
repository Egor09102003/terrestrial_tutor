package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


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

    /**
     * Find las
     * 
     * @return
     */
    @Query(value = "SELECT * FROM homework_solutions WHERE attempt_number=(SELECT MAX(attempt_number) FROM homework_solutions WHERE homework=3073 AND pupil=?2) AND homework=?1  AND pupil=?2", nativeQuery = true)
    AttemptEntity findLastAttempt(Long homework, Long pupil);
}
