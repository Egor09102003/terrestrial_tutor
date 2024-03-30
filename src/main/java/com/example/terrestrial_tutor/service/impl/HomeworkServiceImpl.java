package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.repository.HomeworkRepository;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.repository.TaskRepository;
import com.example.terrestrial_tutor.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public HomeworkEntity addHomework(HomeworkDTO request) {
        // todo получить авторизированного пользователя и установить в tutor
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HomeworkEntity newHomework = new HomeworkEntity();
        newHomework.setName(request.getName());
        newHomework.setTargetTime(request.getTargetTime());
        newHomework.setDeadLine(request.getDeadLine());
        SubjectEntity subject = subjectService.findSubjectByName(request.getSubject());
        newHomework.setSubject(subject);
        List<HomeworkEntity> subjectHomeworks = subject.getHomeworkList();
        subjectHomeworks.add(newHomework);
        subject.setHomeworkList(subjectHomeworks);
        newHomework.setTutor(tutor);
        newHomework.setPupils(request.getPupilIds().stream()
                .map(id -> pupilService.findPupilById(id)).collect(Collectors.toList()));
        if (request.getTasksCheckingTypes() != null) {
            Map<TaskEntity, TaskCheckingType> tasksCheckingTypes = new HashMap<>();
            request.getTasksCheckingTypes().forEach((key, value) ->
            {
                TaskEntity task = taskService.getTaskById(key);
                tasksCheckingTypes.put(task, TaskCheckingType.valueOf(value));
            });
            newHomework.setTasksCheckingTypes(tasksCheckingTypes);
        }
        List<HomeworkEntity> currentTutorHomeworks = tutor.getHomeworkList();
        currentTutorHomeworks.add(newHomework);
        tutor.setHomeworkList(currentTutorHomeworks);
        newHomework = homeworkRepository.save(newHomework);
        tutorService.updateTutor(tutor);
        return newHomework;
    }
    public HomeworkEntity getHomeworkById(Long id){
        return homeworkRepository.getById(id);
    }

    public void deleteHomeworkById(Long id) {
        homeworkRepository.delete(homeworkRepository.findHomeworkEntityById(id));
    }

    public HomeworkEntity save(HomeworkEntity homework) {
        return homeworkRepository.save(homework);
    }
//    public List<HomeworkEntity> getAllHomeworksPupil(){
//        PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return homeworkRepository.findHomeworkEntitiesByPupils(new PupilEntity[]{pupil});
//    }

    public List<HomeworkEntity> getAllHomeworksTutor(){
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return homeworkRepository.findHomeworkEntitiesByTutor(tutor);
    }
}
