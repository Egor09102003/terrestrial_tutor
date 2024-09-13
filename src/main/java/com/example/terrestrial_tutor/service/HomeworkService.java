package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.HomeworkEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для работы с домашними работами
 */
public interface HomeworkService {

    /**
     * Сохранение дз
     *
     * @param homework дз
     * @return сохраненное дз
     */
    HomeworkEntity saveHomework(HomeworkEntity homework);

    /**
     * Поиск всех дз по репетитору
     *
     * @return лист дз
     */
    List<HomeworkEntity> getAllHomeworksTutor();

    /**
     * Поиск всех дз
     *
     * @return лист дз
     */
    List<HomeworkEntity> getAllHomeworks();

    /**
     * Get last pupil answers and create attempt if doesnt exist
     *
     * @param homeworkId id дз
     * @return дз DTO
     */
    HomeworkAnswersDTO initHomework(Long homeworkId);

    /**
     * Поиск дз по id
     *
     * @param id id
     * @return дз
     */
    HomeworkEntity getHomeworkById(Long id);

    /**
     * Удалить дз по id
     *
     * @param id id
     */
    Long deleteHomeworkById(Long id);

    /**
     * Сохранение ответов на дз
     *
     * @param answers    ответы ученика
     * @param idHomework id дз
     * @return дз DTO
     */
    HomeworkAnswersDTO checkingAndSaveAnswers(Map<Long, String> answers, Long idHomework);

    /**
     * Find homeworks by pupil id and subject name
     * 
     * @param pupilId
     * @param subjectName
     * @return
     */
    List<HomeworkEntity> getHomeworksByPupilAndSubject(Long pupilId, String subjectName);

    /**
     * Get homework for current pupil
     * 
     * @param id
     * @return
     */
    HomeworkEntity getHomeworkByIdForCurrentPupil(Long id);

    /**
     * Check all answers and set status finished for attempt
     * 
     * @param answers
     * @param homeworkId
     * @return
     */
    HomeworkAnswersDTO checkAndFinish(Map<Long, String> answers, Long homeworkId);

    /**
     * Get homework right answers
     * 
     * @param homeworkId
     * @return
     */
    HashMap<Long, String> getHomeworkAnswers(Long homeworkId);

    /**
     * Get pupil's completed homeworks
     * 
     * @param id
     * @return
     */
    Map<Long, Long> getCompletedHomework(Long id);

    /**
     * Get last or else attempt
     * 
     * @param homeworkId
     * @param attemptNumber
     * @return
     */
    HomeworkAnswersDTO getPupilAnswers(Long homeworkId, Long pupilId, Optional<Integer> attemptNumber);

    /**
     * Get all attempts
     *
     * @return - all saved attempts
     */
    List<AttemptEntity> getAllAttempts();

    /**
     * Save attempt
     *
     * @param attemptEntity attempt
     */
    void saveAttempt(AttemptEntity attemptEntity);

    /**
     * Set checked statuses for attempt
     *
     * @param updatedAnswers HomeworkAnswersDTO typed answers statuses
     * @param pupilId pupil id
     * @param homeworkId homework id
     * @return HomeworkAnswersDTO typed updated statuses
     * @throws Exception if attempt doesnt exists
     */
    HomeworkAnswersDTO manuallyChecking(
            HomeworkAnswersDTO updatedAnswers,
            Long pupilId,
            Long homeworkId
    ) throws Exception;

    /**
     * Repair homework
     */
    void repairHomeworks();

    /**
     * Repair attempts
     */
    public void repairAttemptNumber();
}
