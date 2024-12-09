package com.example.terrestrial_tutor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;

public interface AttemptService {

    AttemptEntity getLastActiveAttempt(Long homeworkId, PupilEntity pupil);

    AttemptEntity getFinishedAttempt(Optional<Integer> attemptNumber, Long homeworkId, PupilEntity pupil);

    AttemptEntity saveAnswers(AttemptEntity attempt, HashMap<Long, String> answers);

    AttemptEntity finishAttempt(AttemptEntity attempt, HashMap<Long, String> answers);

    List<AttemptEntity> getAll();

}
