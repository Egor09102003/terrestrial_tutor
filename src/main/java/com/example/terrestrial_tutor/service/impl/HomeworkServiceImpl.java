package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.repository.*;
import com.example.terrestrial_tutor.service.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;

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

    public HomeworkAnswersDTO initHomework(Long homeworkId, Optional<Integer> attemptNumber) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AttemptEntity attempt = this.getCurrentAttempt(pupil, homeworkId, attemptNumber);

        if (attempt == null || attempt.getStatus() == HomeworkStatus.FINISHED) {
            this.checkingAndSaveAnswers(new HashMap<>(), homeworkId);
            attempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        }
        
        return this.getAttemptAnswers(attempt);
    }

    public HomeworkAnswersDTO getPupilAnswers(Long homeworkId, Optional<Integer> attemptNumber) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AttemptEntity attempt = this.getCurrentAttempt(pupil, homeworkId, attemptNumber);
        return this.getAttemptAnswers(attempt);
    }

    private AttemptEntity getCurrentAttempt(PupilEntity pupil, Long homeworkId, Optional<Integer> attemptNumber) {
        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
        AttemptEntity attempt;
        if (attemptNumber.isEmpty()) {
            attempt = attemptRepository.findLastAttempt(homework.getId(), pupil.getId());
        } else {
            attempt = attemptRepository.findFirstByPupilAndHomeworkAndAttemptNumber(
                pupil,
                homework,
                attemptNumber.get());
        }
        return attempt;
    }

    private HomeworkAnswersDTO getAttemptAnswers(AttemptEntity attempt) {
        List<TaskEntity> tasks = taskRepository.findAllById(attempt.getAnswers().keySet());
        
        HomeworkAnswersDTO response = new HomeworkAnswersDTO();
        response.setAttemptCount(attempt.getAttemptNumber());
        HashMap<Long, String> answers = attempt.getAnswers();
        HashMap<Long, TaskCheckingType> taskCheckingTypes = this.getHomeworkCheckingTypes(attempt.getHomework().getTaskCheckingTypes());
        for (TaskEntity task : tasks) {
            response.getAnswersStatuses().put(
                task.getId(),
                this.checkAnswer(task, answers.get(task.getId()), taskCheckingTypes.get(task.getId()))
            );
        }

        return response;
    }

    public Map<Long, Long> getCompletedHomework(Long id) {
        PupilEntity pupil = pupilService.findPupilById(id);
        Map<Long, Long> result = new HashMap<>();
        for (HomeworkEntity homework : pupil.getHomeworkList()) {
            List<AttemptEntity> attempts = homework.getAnswerEntities();
            Long pupilAttemptsAmount = attempts.stream().filter(attempt -> Objects.equals(attempt.getPupil().getId(), pupil.getId())).count();
            result.put(homework.getId(), pupilAttemptsAmount);
        }
        return result;
    }

    private HashMap<Long, TaskCheckingType> getHomeworkCheckingTypes(String json) {
        Type type = new TypeToken<HashMap<Long, TaskCheckingType>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    public HomeworkAnswersDTO checkAndFinish(Map<Long, String> answers, Long homeworkId) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HomeworkAnswersDTO response = checkingAndSaveAnswers(answers, homeworkId);
        AttemptEntity lastAttempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        lastAttempt.setStatus(HomeworkStatus.FINISHED);
        attemptRepository.save(lastAttempt);
        return response;
    }

    public HomeworkAnswersDTO checkingAndSaveAnswers(Map<Long, String> answers, Long homeworkId) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AttemptEntity lastAttempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        int lastAttemptNumber = lastAttempt != null ? lastAttempt.getAttemptNumber() : 0;
        if (lastAttempt == null || lastAttempt.getStatus() == HomeworkStatus.FINISHED) {
            lastAttempt = new AttemptEntity();
            lastAttempt.setAttemptNumber(lastAttemptNumber + 1);
            lastAttempt.setSolutionDate(new Date().toInstant().toEpochMilli());
            lastAttempt.setAnswers(new HashMap<>());
        }
        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
        HashMap<Long, TaskCheckingType> checkingTypes = getHomeworkCheckingTypes(homework.getTaskCheckingTypes());
        List<TaskEntity> currentTasks = taskRepository.findAllById(checkingTypes.keySet());
        HashMap<Long, String> savedAnswers = lastAttempt.getAnswers();
        
        HomeworkAnswersDTO response = new HomeworkAnswersDTO();
        response.setAttemptCount(lastAttempt.getAttemptNumber());
        
        for (TaskEntity task : currentTasks) {
            Long taskId = task.getId();
            if (answers.get(taskId) != null) {
                savedAnswers.put(taskId, answers.get(taskId));
                response.getAnswersStatuses().put(
                    taskId, 
                    this.checkAnswer(
                        task, 
                        answers.get(taskId),
                        checkingTypes.get(taskId)));
            }

            if (!savedAnswers.containsKey(taskId)) {
                savedAnswers.put(taskId, "");
            }
        }

        lastAttempt.setHomework(homework);
        lastAttempt.setPupil(pupil);
        lastAttempt.setAnswers(savedAnswers);
        attemptRepository.save(lastAttempt);

        return response;
    }

    public HomeworkAnswersDTO.Status checkAnswer(TaskEntity task, String answer, TaskCheckingType taskCheckingType) {
        HomeworkAnswersDTO.Status taskStatus = new HomeworkAnswersDTO.Status();
        switch (taskCheckingType) {
            case AUTO, INSTANCE:
                switch (task.getAnswerType()) {
                    case VALUE, VARIANTS, CODE, DETAILED:
                        taskStatus.setStatus(answer != null && task.getRightAnswer().trim().equals(answer.trim()));
                        taskStatus.setCurrentAnswer(answer);
                        break;
                    case TABLE:
                        taskStatus.setStatus(checkTable(task.getRightAnswer(),answer));
                        taskStatus.setCurrentAnswer(answer);
                        break;
                    default:
                        break;
                }
                break;
        
            default:
                break;
        }
        return taskStatus;
    }

    public Boolean checkTable(String taskAnswer, String pupilAnswer) {
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

    public List<HomeworkEntity> getAllHomeworksTutor() {
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return homeworkRepository.findHomeworkEntitiesByTutor(tutor);
    }

    public List<HomeworkEntity> getAllHomeworks() {
        return homeworkRepository.findAll();
    }

    public List<HomeworkEntity> getHomeworksByPupilAndSubject(Long pupilId, String subjectName) {
        PupilEntity pupil = pupilService.findPupilById(pupilId);
        SubjectEntity subject = subjectService.findSubjectByName(subjectName);
        return homeworkRepository.findHomeworkEntitiesByPupilsAndSubject(pupil, subject);
    }

    public HomeworkEntity getHomeworkByIdForCurrentPupil(Long id) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return homeworkRepository.findHEntityByIdAndPupils(id, pupil);
    }

    public HashMap<Long, String> getHomeworkAnswers(Long homeworkId) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (attemptRepository.findLastAttempt(homeworkId, pupil.getId()) == null) {
            return new HashMap<>();
        }

        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
        HashMap<Long, String> answers = new HashMap<>();
        List<TaskEntity> tasks = taskRepository.findAllById(this.getHomeworkCheckingTypes(homework.getTaskCheckingTypes()).keySet());
        for (TaskEntity task : tasks) {
            answers.put(task.getId(), task.getAnswer());
        }
        return answers;
    }
}
