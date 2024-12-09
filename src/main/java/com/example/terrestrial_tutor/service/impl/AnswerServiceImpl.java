package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.AnswerEntity;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.repository.AnswerRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.AnswerService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    @NonNull
    AnswerRepository answerRepository;

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public Boolean checkAnswer(AnswerEntity answer) {
        TaskEntity task = answer.getTask();
        try {
            String[] taskAnswers = new Gson().fromJson(task.getAnswer(), String[].class);
            if (taskAnswers.length > 0) {
                switch (task.getAnswerType()) {
                    case VALUE, VARIANTS:
                        return answer.getAnswer().strip().equals(taskAnswers[0].strip());
                    case TABLE:
                        return checkTable(taskAnswers[0], answer.getAnswer());
                    default:
                        break;
                }
            }
        } catch (JsonSyntaxException e) {
            LOG.error("Failed to get from JSON answer of task: " + task.getId(), e);
            return false;
        }
        return false;
    }

    private Boolean checkTable(String taskAnswer, String pupilAnswer) {
        String[][] answerTable = new Gson().fromJson(taskAnswer, String[][].class);
        String[][] pupilTable = new Gson().fromJson(pupilAnswer, String[][].class);
        if (pupilTable == null || answerTable == null || pupilTable.length < answerTable.length) {
            return false;
        }
        for (int i = 0; i < pupilTable.length; i++) {
            if (pupilTable[i].length < answerTable[i].length) {
                return false;
            }
            for (int j = 0; j < pupilTable[i].length; j++) {
                if ((!pupilTable[i][j].trim().isEmpty() && j >= answerTable[i].length)
                || (answerTable[i].length > j 
                && !answerTable[i][j].trim().equals(pupilTable[i][j].trim()))) 
                {
                    return false;
                }
            } 
        }
        return true;
    }

    public AnswerEntity findAnswerByAttemptAndTask(AttemptEntity attempt, TaskEntity task) {
        return answerRepository.findFirstByAttemptAndTask(attempt, task);
    }

    public AnswerEntity save(AnswerEntity answer) {
        return answerRepository.save(answer);
    }

    public List<AnswerEntity> saveAll(List<AnswerEntity> answers) {
        return answerRepository.saveAll(answers);
    }
    
}
