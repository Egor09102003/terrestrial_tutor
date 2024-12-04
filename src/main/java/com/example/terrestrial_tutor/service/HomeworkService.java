package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.dto.TaskCheckingDTO;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;

import java.util.*;

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
    HomeworkEntity saveHomework(HomeworkEntity homework, LinkedHashMap<Long, TaskCheckingDTO> taskCheckingTypes);

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
     * Get homework right answers
     * 
     * @param homeworkId
     * @return
     */
    HashMap<Long, String> getHomeworkAnswers(Long homeworkId);

    Set<HomeworkEntity> getCompletedHomework(PupilEntity pupil);
}
