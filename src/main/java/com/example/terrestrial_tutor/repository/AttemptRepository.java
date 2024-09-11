package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.AttemptEntity;
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
    AttemptEntity findFirstByPupilIdAndHomeworkIdAndAttemptNumber(Long pupil, Long homework, int number);

    /**
     * Find last pupil attempt in homework
     *
     */
    @Query(value = "SELECT * FROM homework_solutions WHERE attempt_number=(SELECT MAX(attempt_number) FROM homework_solutions WHERE homework=?1 AND pupil=?2) AND homework=?1  AND pupil=?2 LIMIT 1", nativeQuery = true)
    AttemptEntity findLastAttempt(Long homework, Long pupil);

    /**
     * Find last pupil attempt in homework
     *
     */
    @Query(value = "SELECT * FROM homework_solutions WHERE attempt_number=(SELECT MAX(attempt_number) FROM homework_solutions WHERE homework=?1 AND pupil=?2 AND status=0) AND homework=?1  AND pupil=?2 LIMIT 1", nativeQuery = true)
    AttemptEntity findLastFinishedAttempt(Long homework, Long pupil);
}
