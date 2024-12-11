package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import com.example.terrestrial_tutor.exceptions.AttemptFinishedException;
import com.example.terrestrial_tutor.repository.AttemptRepository;
import com.example.terrestrial_tutor.repository.HomeworkRepository;
import com.example.terrestrial_tutor.service.AnswerService;
import com.example.terrestrial_tutor.service.AttemptService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    @NonNull
    AttemptRepository attemptRepository;

    @NonNull
    HomeworkRepository homeworkRepository;

    @NonNull
    AnswerService answerService;

    public static final Logger LOG = LoggerFactory.getLogger(AttemptServiceImpl.class);

    public AttemptEntity getLastActiveAttempt(Long homeworkId, PupilEntity pupil) {
        AttemptEntity attempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        HomeworkEntity homework = homeworkRepository.findById(homeworkId).orElse(null);
        if (homework == null) {
            throw new EntityExistsException("No such homework.");
        }
        if (attempt == null) {
            attempt = new AttemptEntity(pupil, 1, homework);
        }
        if (attempt.getStatus() == HomeworkStatus.FINISHED) {
            attempt = new AttemptEntity(pupil, attempt.getAttemptNumber() + 1, homework);
        }
        return save(attempt);
    }

    public AttemptEntity getFinishedAttempt(Optional<Integer> attemptNumber, Long homeworkId, PupilEntity pupil) {
        if (attemptNumber.isPresent()) {
            return attemptRepository.findFirstByPupilIdAndHomeworkIdAndAttemptNumberAndStatus(
                pupil.getId(),     
                homeworkId, 
                attemptNumber.get(), 
                HomeworkStatus.FINISHED
            );
        }
        return attemptRepository.findLastFinishedAttempt(homeworkId, pupil.getId());
    }

    public AttemptEntity save(AttemptEntity attempt) {
        return attemptRepository.save(attempt);
    }

    public AttemptEntity findAttemptById(Long id) {
        return attemptRepository.findById(id).orElse(null);
    }

    public AttemptEntity saveAnswers(AttemptEntity attempt, HashMap<Long, String> answers) {
        if (attempt.getStatus() != HomeworkStatus.FINISHED) {
            Map<Long, TaskCheckingEntity> taskCheckingTypes = attempt.getHomework().getTaskCheckingTypes();
            attempt.setAttemptPoints(0L);
            for (Long taskId : taskCheckingTypes.keySet()) {
                AnswerEntity currentAnswer = attempt.getAnswers().get(taskId);
                if (answers.containsKey(taskId) && !answers.get(taskId).isEmpty()) {
                    if (currentAnswer == null) {
                        currentAnswer = new AnswerEntity(attempt, null, taskCheckingTypes.get(taskId).getTask());
                    }
                    currentAnswer.setAnswer(answers.get(taskId));
                    Boolean isRight = answerService.checkAnswer(currentAnswer);
                    if (isRight) {
                        attempt.setAttemptPoints(
                                attempt.getAttemptPoints() +
                                        taskCheckingTypes.get(taskId).getTask().getCost().longValue()
                        );
                    }
                    currentAnswer.setPoints(isRight ? currentAnswer.getTask().getCost() : 0);
                    switch (taskCheckingTypes.get(taskId).getCheckingType()) {
                        case AUTO, INSTANCE:
                            currentAnswer.setStatus(isRight ? TaskStatuses.RIGHT : TaskStatuses.WRONG);
                            break;
                        default:
                            currentAnswer.setStatus(TaskStatuses.ON_CHECKING);
                            break;
                    }
                } else {
                    continue;
                }
                attempt.getAnswers().put(taskId, currentAnswer);
            }

            return save(attempt);
        } else {
            throw new AttemptFinishedException("Attempt has already been finished.");
        }
    } 

    public AttemptEntity finishAttempt(AttemptEntity attempt, HashMap<Long, String> answers) {
        attempt = saveAnswers(attempt, answers);
        Long points = 0L;
        for (AnswerEntity answer : attempt.getAnswers().values()) {
            points += answer.getPoints();
        }
        attempt.setStatus(HomeworkStatus.FINISHED);
        attempt.setAttemptPoints(points);
        return save(attempt);
    }

    public List<AttemptEntity> getAll() {
        return attemptRepository.findAll();
    }
}