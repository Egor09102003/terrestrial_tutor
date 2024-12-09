package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.AnswerDTO;
import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.dto.TaskCheckingDTO;
import com.example.terrestrial_tutor.dto.facade.AnswerMapper;
import com.example.terrestrial_tutor.dto.facade.TaskCheckingMapper;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.service.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@Api
public class RepairController {

    @NonNull
    private final AttemptService attemptService;

    @NonNull
    private final TaskService taskService;

    @NonNull
    private final AnswerMapper answerMapper;

    @NonNull
    private final AnswerService answerService;

    @NonNull
    private final HomeworkService homeworkService;

    @NonNull
    private final TaskCheckingService taskCheckingService;

    @NonNull
    private final TaskCheckingMapper taskCheckingMapper;

    @PatchMapping("/repair/answers")
    public List<AnswerDTO> repairPupilAnswers() {
        List<AnswerDTO> answerDTOS = new ArrayList<>();
        List<AnswerEntity> answers = new ArrayList<>();
        List<AttemptEntity> attempts = attemptService.getAll();
        for (AttemptEntity attempt : attempts) {
            HomeworkAnswersDTO answersLegacy = attempt.getAnswersLegacy();
            AnswerEntity answer = new AnswerEntity();
            for (Map.Entry<Long, HomeworkAnswersDTO.Status> entry : answersLegacy.getAnswersStatuses().entrySet()) {
                answer.setAttempt(attempt);
                answer.setAnswer(entry.getValue().getCurrentAnswer());
                answer.setStatus(entry.getValue().getStatus());
                answer.setPoints(entry.getValue().getPoints());
                answer.setTask(taskService.getTaskById(entry.getKey()));
                answers.add(answer);
                answerDTOS.add(answerMapper.answerToAnswerDTO(answer, true));
            }
        }

        answerService.saveAll(answers);
        return answerDTOS;
    }

    @PatchMapping("/repair/tasks")
    public List<TaskCheckingDTO> repairTasksChecking() {
        List<TaskCheckingDTO> taskCheckingDTOS = new ArrayList<>();
        List<TaskCheckingEntity> taskCheckingEntities = new ArrayList<>();
        List<HomeworkEntity> homeworks = homeworkService.getAllHomeworks();
        for (HomeworkEntity homework : homeworks) {
            int i = 0;
            for (Map.Entry<String, String> entry : homework.getTaskCheckingTypesLegacy().entrySet()) {
                TaskEntity task = taskService.getTaskById(Long.parseLong(entry.getKey()));
                if (task != null) {
                    TaskCheckingEntity taskChecking = new TaskCheckingEntity();
                    taskChecking.setHomework(homework);
                    taskChecking.setTask(task);
                    taskChecking.setCheckingType(TaskCheckingType.valueOf(entry.getValue()));
                    taskChecking.setOrderIndex(i);
                    taskCheckingEntities.add(taskChecking);
                    taskCheckingDTOS.add(taskCheckingMapper.toTaskCheckingDTO(taskChecking, true));
                    i++;
                }
            }
        }
        taskCheckingService.saveAll(taskCheckingEntities);
        return taskCheckingDTOS;
    }
}
