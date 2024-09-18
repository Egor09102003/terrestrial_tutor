package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.AnswerTypes;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import com.example.terrestrial_tutor.repository.*;
import com.example.terrestrial_tutor.service.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);

    public HomeworkEntity saveHomework(HomeworkEntity homework) {
        homework = homeworkRepository.save(homework);
        for (TutorEntity tutor : homework.getTutors()) {
            tutorService.updateTutor(tutor);
        }
        return homework;
    }

    public HomeworkEntity getHomeworkById(Long id) {
        return homeworkRepository.findHomeworkEntityById(id);
    }

    public Long deleteHomeworkById(Long id) {
        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(id);
        for (TutorEntity tutor : homework.getTutors()) {
            tutor.getHomeworkList().remove(homework);
            tutorService.updateTutor(tutor);
        }
        homeworkRepository.deleteById(id);
        return id;
    }

    public HomeworkAnswersDTO initHomework(Long homeworkId) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AttemptEntity attempt = this.getCurrentAttempt(pupil.getId(), homeworkId, Optional.empty());
        HomeworkAnswersDTO attemptAnswers;

        if (attempt == null || attempt.getStatus() == HomeworkStatus.FINISHED) {
            attemptAnswers = this.checkingAndSaveAnswers(new HashMap<>(), homeworkId);
        } else {
            attemptAnswers = attempt.getAnswers();
        }
        
        return attemptAnswers;
    }

    public HomeworkAnswersDTO getPupilAnswers(Long homeworkId, Long pupilId, Optional<Integer> attemptNumber) {
        AttemptEntity attempt;
        if (attemptNumber.isPresent()) {
            attempt = this.attemptRepository.findFirstByPupilIdAndHomeworkIdAndAttemptNumber(pupilId, homeworkId, attemptNumber.get());
        } else {
            attempt = this.attemptRepository.findLastFinishedAttempt(homeworkId, pupilId);
        }
        if (attempt == null) {
            return new HomeworkAnswersDTO();
        }
        HomeworkAnswersDTO answers = attempt.getAnswers();
        if (answers.getAttemptCount() == -1) {
            for (Long taskId : answers.getAnswersStatuses().keySet()) {
                TaskCheckingType taskCheckingType = getHomeworkCheckingTypes(
                        attempt.getHomework().getTaskCheckingTypes()
                ).get(taskId);
                answers.getAnswersStatuses().put(
                        taskId,
                        checkAnswer(
                            taskService.getTaskById(taskId),
                            answers.getAnswersStatuses().get(taskId).getCurrentAnswer(),
                            taskCheckingType
                        )
                );
            }
            answers.setAttemptCount(attempt.getAttemptNumber());
            attempt.setAnswers(answers);
            this.saveAttempt(attempt);
        }
        return attempt.getAnswers();
    }

    private AttemptEntity getCurrentAttempt(Long pupilId, Long homeworkId, Optional<Integer> attemptNumber) {
        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
        AttemptEntity attempt;
        if (attemptNumber.isEmpty()) {
            attempt = attemptRepository.findLastAttempt(homework.getId(), pupilId);
        } else {
            attempt = attemptRepository.findFirstByPupilIdAndHomeworkIdAndAttemptNumber(
                    pupilId,
                    homework.getId(),
                    attemptNumber.get());
        }
        return attempt;
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
        checkingAndSaveAnswers(answers, homeworkId);
        AttemptEntity lastAttempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        lastAttempt.setStatus(HomeworkStatus.FINISHED);
        attemptRepository.save(lastAttempt);
        return lastAttempt.getAnswers();
    }

    public HomeworkAnswersDTO checkingAndSaveAnswers(Map<Long, String> answers, Long homeworkId) {
        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AttemptEntity lastAttempt = attemptRepository.findLastAttempt(homeworkId, pupil.getId());
        int lastAttemptNumber = lastAttempt != null ? lastAttempt.getAttemptNumber() : 0;
        if (lastAttempt == null || lastAttempt.getStatus() == HomeworkStatus.FINISHED) {
            lastAttempt = new AttemptEntity();
            lastAttempt.setAttemptNumber(lastAttemptNumber + 1);
            lastAttempt.setSolutionDate(new Date().toInstant().toEpochMilli());
            HomeworkAnswersDTO newAnswers = new HomeworkAnswersDTO();
            newAnswers.setAttemptCount(lastAttemptNumber + 1);
            lastAttempt.setAnswers(newAnswers);
        }

        try {
            HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
            HashMap<Long, TaskCheckingType> checkingTypes = getHomeworkCheckingTypes(homework.getTaskCheckingTypes());
            List<TaskEntity> currentTasks = taskRepository.findAllById(checkingTypes.keySet());
            HomeworkAnswersDTO savedAnswers = lastAttempt.getAnswers();

            for (TaskEntity task : currentTasks) {
                Long taskId = task.getId();
                if (answers.get(taskId) != null) {
                    HomeworkAnswersDTO.Status status = this.checkAnswer(
                            task,
                            answers.get(taskId),
                            checkingTypes.get(taskId)
                    );
                    savedAnswers.getAnswersStatuses().put(taskId, status);
                }

                if (!savedAnswers.getAnswersStatuses().containsKey(taskId)) {
                    savedAnswers.getAnswersStatuses().put(taskId, new HomeworkAnswersDTO.Status());
                }
            }

            lastAttempt.setHomework(homework);
            lastAttempt.setPupil(pupil);
            lastAttempt.setAnswers(savedAnswers);
            attemptRepository.save(lastAttempt);

            return savedAnswers;
        } catch (Exception e) {
            throw new NoSuchElementException("Save and get homework answers failed");
        }
    }

    public HomeworkAnswersDTO.Status checkAnswer(TaskEntity task, String answer, TaskCheckingType taskCheckingType) {
        HomeworkAnswersDTO.Status taskStatus = new HomeworkAnswersDTO.Status();
        switch (taskCheckingType) {
            case AUTO, INSTANCE:
                switch (task.getAnswerType()) {
                    case VALUE, VARIANTS, CODE, DETAILED:
                        taskStatus.setStatus(
                                answer != null && task.getRightAnswer().trim().equals(answer.trim())
                                ? TaskStatuses.RIGHT : TaskStatuses.WRONG
                        );
                        break;
                    case TABLE:
                        taskStatus.setStatus(
                                checkTable(task.getRightAnswer(),answer)
                                ? TaskStatuses.RIGHT : TaskStatuses.WRONG
                        );
                        break;
                    default:
                        break;
                }
                break;
            case MANUALLY:
                taskStatus.setStatus(TaskStatuses.ON_CHECKING);
            default:
                break;
        }
        if (taskStatus.getStatus() == TaskStatuses.RIGHT) {
            taskStatus.setPoints(task.getCost());
        }
        taskStatus.setCurrentAnswer(answer);
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
        return homeworkRepository.findHomeworkEntitiesByTutors(tutor);
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() == ERole.PUPIL) {
            PupilEntity pupil = (PupilEntity) user;
            if (attemptRepository.findLastAttempt(homeworkId, pupil.getId()) == null) {
                return new HashMap<>();
            }
        }

        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
        HashMap<Long, String> answers = new HashMap<>();
        List<TaskEntity> tasks = taskRepository.findAllById(this.getHomeworkCheckingTypes(homework.getTaskCheckingTypes()).keySet());
        for (TaskEntity task : tasks) {
            answers.put(task.getId(), task.getAnswer());
        }
        return answers;
    }

    public void saveAttempt(AttemptEntity attemptEntity) {
        attemptRepository.save(attemptEntity);
    }

    public List<AttemptEntity> getAllAttempts() {
        return attemptRepository.findAll();
    }

    public HomeworkAnswersDTO manuallyChecking(
            HomeworkAnswersDTO updatedAnswers,
            Long pupilId,
            Long homeworkId
    ) throws Exception {
        try {
            AttemptEntity attempt = attemptRepository.findFirstByPupilIdAndHomeworkIdAndAttemptNumber(
                    pupilId,
                    homeworkId,
                    updatedAnswers.getAttemptCount()
            );

            if (attempt == null) {
                throw new NoSuchElementException("Attempt not found");
            }

            HomeworkAnswersDTO attemptAnswers = attempt.getAnswers();
            HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
            if (homework == null) {
                throw new NoSuchElementException("Homework not found");
            }
            HashMap<Long, TaskCheckingType> taskCheckingTypes = this.getHomeworkCheckingTypes(homework.getTaskCheckingTypes());
            for (Map.Entry<Long,HomeworkAnswersDTO.Status> status : updatedAnswers.getAnswersStatuses().entrySet()) {
                if(attemptAnswers.getAnswersStatuses().containsKey(status.getKey())
                        && taskCheckingTypes.containsKey(status.getKey())
                ) {
                    HomeworkAnswersDTO.Status updatedStatus = attemptAnswers.getAnswersStatuses().get(status.getKey());
                    updatedStatus.setPoints(status.getValue().getPoints());
                    updatedStatus.setStatus(status.getValue().getStatus());
                    attemptAnswers.getAnswersStatuses().put(status.getKey(), updatedStatus);
                }
            }
            attempt.setAnswers(attemptAnswers);
            attemptRepository.save(attempt);
            return attemptAnswers;
        } catch (Exception e) {
            throw new Exception("Tasks status update failed: " +  e.getMessage());
        }
    }

    public void repairHomeworks() {
        List<AttemptEntity> allAttempts = attemptRepository.findAll();
        for(AttemptEntity attempt : allAttempts) {
            try {
                Type type = new TypeToken<HashMap<Long, String>>(){}.getType();
                HashMap<Long, String> answers = new Gson().fromJson(attempt.getJSONAnswers(), type);
                HomeworkAnswersDTO formatedAnswers = new HomeworkAnswersDTO();
                List<TaskEntity> tasks = taskRepository.findAllById(answers.keySet());
                for (Long taskId : answers.keySet()) {
                    HomeworkAnswersDTO.Status status = new HomeworkAnswersDTO.Status();
                    status.setCurrentAnswer(answers.get(taskId));
                    TaskEntity currentTask = tasks.stream().filter(task -> task.getId().equals(taskId)).findFirst().get();
                    if (status.getCurrentAnswer().equals(currentTask.getAnswer())) {
                        status.setStatus(TaskStatuses.RIGHT);
                        status.setPoints(currentTask.getCost());
                    }
                    formatedAnswers.getAnswersStatuses().put(taskId, status);
                }
                attempt.setAnswers(formatedAnswers);
                attemptRepository.save(attempt);
            } catch (JsonSyntaxException e) {
                log.error("Failed to repair attempt: " + attempt.getId(), e);
                continue;
            }
        }
    }

    public void repairAttemptNumber() {
        List<PupilEntity> pupils = pupilService.findAllPupils();
        for (PupilEntity pupil : pupils) {
            if (pupil.getAnswers() != null) {
                try {
                    HashMap<Long, List<AttemptEntity>> homeworkPupilAttempts = new HashMap<>();
                    for (AttemptEntity attempt : pupil.getAnswers()) {
                        Long homeworkId = attempt.getHomework().getId();
                        List<AttemptEntity> homeworkAttempts = new ArrayList<>();
                        if (homeworkPupilAttempts.get(homeworkId) != null) {
                            homeworkAttempts = homeworkPupilAttempts.get(homeworkId);
                            homeworkAttempts.add(attempt);
                        } else {
                            homeworkAttempts.add(attempt);
                        }
                        homeworkPupilAttempts.put(attempt.getHomework().getId(), homeworkAttempts);
                    }

                    for (Long homeworkId : homeworkPupilAttempts.keySet()) {
                        List<AttemptEntity> homeworkAttempts = homeworkPupilAttempts.get(homeworkId);
                        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
                        HashMap<Long, TaskCheckingType> taskCheckingTypes = this.getHomeworkCheckingTypes(homework.getTaskCheckingTypes());
                        for (int i = 0; i < homeworkAttempts.size(); i++) {
                            AttemptEntity currentAttempt = homeworkAttempts.get(i);
                            currentAttempt.setAttemptNumber(i + 1);
                            HomeworkAnswersDTO answers = currentAttempt.getAnswers();
                            answers.setAttemptCount(i + 1);
                            currentAttempt.setAnswers(answers);
                            HashMap<Long, HomeworkAnswersDTO.Status> answersDTO = currentAttempt.getAnswers().getAnswersStatuses();
                            HomeworkAnswersDTO answerStatuses = currentAttempt.getAnswers();
                            for (Long taskId : answersDTO.keySet()) {
                                TaskEntity task = taskService.getTaskById(taskId);
                                HomeworkAnswersDTO.Status status = checkAnswer(task, answersDTO.get(taskId).getCurrentAnswer(), taskCheckingTypes.get(taskId));
                                answerStatuses.getAnswersStatuses().put(taskId, status);
                                if (pupil.getId() == 1205 && homework.getId() == 2174) {
                                    log.info(new Gson().toJson(answerStatuses));
                                }
                            }
                            currentAttempt.setAnswers(answerStatuses);
                            attemptRepository.save(currentAttempt);
                        }
                    }
                } catch (Exception e) {
                    log.error(pupil.getId().toString() + e.getMessage());
                    continue;
                }
            }
        }
    }

    public AttemptEntity getLastFinishedAttempt(Long homeworkId, Long pupilId) {
        return attemptRepository.findLastFinishedAttempt(homeworkId, pupilId);
    }
}
