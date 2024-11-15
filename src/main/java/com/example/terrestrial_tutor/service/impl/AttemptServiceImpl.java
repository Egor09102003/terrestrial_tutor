package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.AnswerEntity;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.TaskCheckingEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import com.example.terrestrial_tutor.repository.AttemptRepository;
import com.example.terrestrial_tutor.repository.HomeworkRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.AnswerService;
import com.example.terrestrial_tutor.service.AttemptService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    @NonNull
    AttemptRepository attemptRepository;

    @NonNull
    HomeworkRepository homeworkRepository;

    @NonNull
    AnswerService answerService;

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public AttemptEntity getLastActiveAttempt(Long homeworkId, PupilEntity pupil) {
        AttemptEntity attempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        HomeworkEntity homewrok = homeworkRepository.findById(homeworkId).orElse(null);
        if (homewrok == null) {
            throw new EntityExistsException("No such homework.");
        }
        if (attempt == null) {
            attempt = new AttemptEntity(pupil, 1, homewrok);
            save(attempt);
        }
        if (attempt.getStatus() == HomeworkStatus.FINISHED) {
            attempt = new AttemptEntity(pupil, attempt.getAttemptNumber() + 1, homewrok);
            initializeAttemptAnswers(attempt);
            save(attempt);
        }
        return attempt;
    }

    private AttemptEntity initializeAttemptAnswers(AttemptEntity attempt) {
        List<AnswerEntity> answers = new ArrayList<>();
        for (TaskEntity task : attempt.getHomework().getTasks()) {
            answers.add(new AnswerEntity(attempt, null, task));
        }
        attempt.setAnswers(answers);
        return attempt;
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
        List<TaskCheckingEntity> taskCheckingtypes = attempt.getHomework().getTaskCheckingTypes();
        for (int i = 0; i < attempt.getAnswers().size(); i++) {
            AnswerEntity currentAnswer = attempt.getAnswers().get(i);
            String answerString = answers.get(currentAnswer.getTask().getId());
            currentAnswer.setAnswer(answerString == null ? currentAnswer.getAnswer() : answerString);
            TaskCheckingEntity checking = taskCheckingtypes.stream()
                    .filter(type -> type.getTask().getId().equals(currentAnswer.getTask().getId()))
                    .findFirst().orElse(null);
            if (checking != null && checking.getCheckingType().equals(TaskCheckingType.INSTANCE)) {
                if (answerService.checkAnswer(currentAnswer)) {
                    currentAnswer.setStatus(TaskStatuses.RIGHT);
                    currentAnswer.setPoints(currentAnswer.getTask().getCost());
                } else {
                    currentAnswer.setStatus(TaskStatuses.WRONG);
                    currentAnswer.setPoints(0);
                }
                attempt.getAnswers().set(i, currentAnswer);
            }
            
        }
        return save(attempt);
    } 

    public AttemptEntity finishAttempt(AttemptEntity attempt, HashMap<Long, String> answers) {
        List<TaskCheckingEntity> taskCheckingtypes = attempt.getHomework().getTaskCheckingTypes();
        for (int i = 0; i < attempt.getAnswers().size(); i++) {
            AnswerEntity currentAnswer = attempt.getAnswers().get(i);
            String answerString = answers.get(currentAnswer.getTask().getId());
            currentAnswer.setAnswer(answerString == null ? currentAnswer.getAnswer() : answerString);
            TaskCheckingEntity checking = taskCheckingtypes.stream()
                    .filter(type -> type.getTask().getId().equals(currentAnswer.getTask().getId()))
                    .findFirst().orElse(null);
            if (checking != null) {
                Boolean isRight = answerService.checkAnswer(currentAnswer);
                currentAnswer.setPoints(isRight ? currentAnswer.getTask().getCost() : 0);
                switch (checking.getCheckingType()) {
                    case AUTO, MANUALLY:
                        currentAnswer.setStatus(isRight ? TaskStatuses.RIGHT : TaskStatuses.WRONG);
                        break;
                    default:
                        currentAnswer.setStatus(TaskStatuses.ON_CHECKING);
                        break;
                }
            }
            attempt.getAnswers().set(i, currentAnswer);
            
        }
        attempt.setStatus(HomeworkStatus.FINISHED);
        return save(attempt);
    }
}
