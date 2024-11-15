package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.repository.*;
import com.example.terrestrial_tutor.service.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    @NonNull
    HomeworkRepository homeworkRepository;

    @NonNull
    PupilService pupilService;

    @NonNull
    TutorService tutorService;

    @NonNull
    SubjectService subjectService;

    @NonNull
    AttemptService attemptService;

    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);

    public HomeworkEntity saveHomework(HomeworkEntity homework) {
        homework = homeworkRepository.save(homework);
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
            if (attemptService.getFinishedAttempt(Optional.empty(), homeworkId, pupil) == null) {
                return new HashMap<>();
            }
        }

        HomeworkEntity homework = homeworkRepository.findHomeworkEntityById(homeworkId);
        HashMap<Long, String> answers = new HashMap<>();
        List<TaskEntity> tasks = homework.getTasks();
        for (TaskEntity task : tasks) {
            answers.put(task.getId(), task.getAnswer());
        }
        return answers;
    }

    public Set<HomeworkEntity> getCompletedHomework(PupilEntity pupil) {
        Set<HomeworkEntity> completedHomeworks = new HashSet<>();
        for (AttemptEntity attempt : pupil.getHomeworkAttempts()) {
            completedHomeworks.add(attempt.getHomework());
        }
        return completedHomeworks;
    }
}
