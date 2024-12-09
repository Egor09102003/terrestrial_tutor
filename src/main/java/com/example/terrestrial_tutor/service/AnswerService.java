package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.AnswerEntity;

import java.util.List;

public interface AnswerService {

    Boolean checkAnswer(AnswerEntity answer);

    AnswerEntity save(AnswerEntity answer);

    List<AnswerEntity> saveAll(List<AnswerEntity> answers);

}
