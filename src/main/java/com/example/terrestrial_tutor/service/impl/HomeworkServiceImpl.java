package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.exceptions.CustomException;
import com.example.terrestrial_tutor.repository.*;
import com.example.terrestrial_tutor.service.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    PupilRepository pupilRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    PupilService pupilService;

    @Autowired
    TutorService tutorService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    AttemptRepository attemptRepository;

    @Autowired
    HomeworkFacade homeworkFacade;

    public HomeworkEntity saveHomework(HomeworkEntity homework) {
        if (!homework.getTutor().getHomeworkList().contains(homework)) {
            Set<HomeworkEntity> currentTutorHomeworks = homework.getTutor().getHomeworkList();
            currentTutorHomeworks.add(homework);
            homework.getTutor().setHomeworkList(currentTutorHomeworks);
        }
        homework = homeworkRepository.save(homework);
        tutorService.updateTutor(homework.getTutor());
        return homework;
    }

    public HomeworkEntity getHomeworkById(Long id) {
        return homeworkRepository.findHomeworkEntityById(id);
    }

    public void deleteHomeworkById(Long id) {
        HomeworkEntity homework = getHomeworkById(id);
        List<AttemptEntity> attempts = homework.getAnswerEntities();
        for (AttemptEntity attempt : attempts) {
            attempt.setHomework(null);
            attemptRepository.save(attempt);
        }
        TutorEntity tutor = homework.getTutor();
        tutor.getHomeworkList().remove(homework);
        tutorService.updateTutor(tutor);
        homeworkRepository.delete(homeworkRepository.findHomeworkEntityById(id));
    }

    public HomeworkEntity save(HomeworkEntity homework) {
        return homeworkRepository.save(homework);
    }

    public HomeworkAnswersDTO getPupilAnswers(Long homeworkId, Long pupilId, int attempt) {
        PupilEntity pupilEntity = pupilService.findPupilById(pupilId);
        HomeworkEntity homework = getHomeworkById(homeworkId);
        AttemptEntity currentAttempt = attemptRepository.findAttemptEntityByPupilAndHomeworkAndAttemptNumber(pupilEntity, homework, attempt);
        int attempts = getLastAttempt(homework, pupilEntity);
        Map<Long, HomeworkAnswersDTO.DetailsAnswer> checkingAnswers = new HashMap<>();
        Map<Long, String> currentAttemptAnswers = new Gson().fromJson(currentAttempt.getAnswers(), (new TypeToken<Map<Long, String>>() {}.getType()));
        if (attempts >= attempt) {
            for (Map.Entry<Long, String> answer : currentAttemptAnswers.entrySet()) {
                String rightAnswer = getTaskAnswerFromJson(homework.getSubject().getTasks().stream().filter(task ->
                        task.getId().equals(answer.getKey())).toList().get(0).getAnswer());
                checkingAnswers.put(answer.getKey(), new HomeworkAnswersDTO.DetailsAnswer(answer.getValue(), rightAnswer));
            }
        } else {
            throw new CustomException("Attempt number not found");
        }
        return new HomeworkAnswersDTO(checkingAnswers, attempt);

    }

    private String getTaskAnswerFromJson(String json) {
        return new Gson().fromJson(json, LinkedList.class).get(0).toString();
    }

    public Map<Long, Integer> getCompletedHomework(Long id) {
        PupilEntity pupil = pupilService.findPupilById(id);
        Set<PupilEntity> pupilEntities = new HashSet<>();
        pupilEntities.add(pupil);
        List<HomeworkEntity> homeworkEntities = homeworkRepository.findHomeworkEntitiesByPupilsIn(pupilEntities);
        Map<Long, Integer> result = new HashMap<>();
        for (HomeworkEntity he : homeworkEntities) {
            int attempts = getLastAttempt(he, pupil);
            if (attempts > 0) {
                result.put(he.getId(), attempts);
            }
        }
        return result;
    }

    public int getLastAttempt(HomeworkEntity homework, PupilEntity pupil) {
        List<AttemptEntity> answers = homework.getAnswerEntities().stream().filter(answerEntity -> Objects.equals(answerEntity.getPupil().getId(), pupil.getId())).toList();
        if (answers.isEmpty()) {
            return 0;
        } else {
            int lastAttempt = 0;
            for (AttemptEntity answer : answers) {
                if (answer.getAttemptNumber() > lastAttempt) {
                    lastAttempt = answer.getAttemptNumber();
                }
            }
            return lastAttempt;
        }
    }

    public HomeworkAnswersDTO checkingAndSaveAnswers(Map<Long, String> answers, Long idHomework, int attempt) {
        HomeworkEntity homework = getHomeworkById(idHomework);
        Map<Long, HomeworkAnswersDTO.DetailsAnswer> checkingAnswers = new HashMap<>();
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AttemptEntity currentAttempt = new AttemptEntity();
        currentAttempt.setPupil(pupil);
        currentAttempt.setAttemptNumber(attempt);
        currentAttempt.setHomework(homework);
        currentAttempt.setAnswers(new Gson().toJson(answers));

        AttemptEntity attemptEntity = attemptRepository.findAttemptEntityByPupilAndHomeworkAndAttemptNumber(pupil, homework, attempt);
        Map<Long, String> currentAttemptAnswers = answers;
        if (attemptEntity != null) {
            currentAttempt.setId(attemptEntity.getId());
            currentAttemptAnswers = new Gson().fromJson(attemptEntity.getAnswers(), (new TypeToken<Map<Long, String>>() {}.getType()));
        }

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Map<Long, String> checkingTypes = new Gson().fromJson(homework.getTaskCheckingTypes(), (new TypeToken<Map<Long, String>>() {}.getType()));
            TaskCheckingType check = TaskCheckingType.valueOf(checkingTypes.get(entry.getKey()));
            TaskEntity task = homework.getSubject().getTasks().stream().filter(taskEntity ->
                    taskEntity.getId().equals(entry.getKey())).findFirst().get();
            String rightAnswer = getTaskAnswerFromJson(task.getAnswer());
            switch (check.name()) {
                case "AUTO":
                    switch (task.getAnswerType()) {
                        case VARIANTS, VALUE:
                            currentAttemptAnswers.put(task.getId(), entry.getValue());
                            checkingAnswers.put(entry.getKey(), new HomeworkAnswersDTO.DetailsAnswer(entry.getValue(), rightAnswer));
                            break;
                        default:
                            throw new CustomException("Task does not have a answer type");
                    }
                    break;
                case "INSTANCE":
                    switch (task.getAnswerType()) {
                        case VARIANTS, VALUE:
                            checkingAnswers.put(entry.getKey(), new HomeworkAnswersDTO.DetailsAnswer(entry.getValue(), rightAnswer));
                            if (attemptEntity != null && rightAnswer.equals(currentAttemptAnswers.get(task.getId()))) {
                                break;
                            }
                            currentAttemptAnswers.put(task.getId(), entry.getValue());
                            break;
                        default:
                            throw new CustomException("Task does not have a answer type");
                    }
                    break;
                //todo сделать другие типы проверок
                case "MANUALLY":
                default:
                    throw new CustomException("Task does not have a review type");
            }
        }
        currentAttempt.setAnswers(new Gson().toJson(currentAttemptAnswers));
        attemptRepository.saveAndFlush(currentAttempt);

        HomeworkAnswersDTO homeworkAnswersDTO = new HomeworkAnswersDTO();
        homeworkAnswersDTO.setCheckingAnswers(checkingAnswers);
        homeworkAnswersDTO.setAttemptCount(attempt);
        return homeworkAnswersDTO;
    }

    public List<HomeworkEntity> getAllHomeworksTutor() {
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return homeworkRepository.findHomeworkEntitiesByTutor(tutor);
    }

    public List<HomeworkEntity> getAllHomeworks() {
        return homeworkRepository.findAll();
    }
}
