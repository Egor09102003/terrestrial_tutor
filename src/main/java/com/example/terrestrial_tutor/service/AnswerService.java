package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.AnswerEntity;

public interface AnswerService {

    Boolean checkAnswer(AnswerEntity answer);

    AnswerEntity save(AnswerEntity answer);

}
