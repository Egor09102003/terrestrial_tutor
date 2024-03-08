package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.exceptions.CustomException;
import com.example.terrestrial_tutor.repository.SubjectRepository;
import com.example.terrestrial_tutor.repository.TaskRepository;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SubjectService subjectService;

    /**
     * Функция вывода листа заданий по учебному прдмету и уровню выбора 1
     *
     * @param subject - учебный предмет
     * @param level1  - уровень выбора 1
     * @return Лист заданий по учебному прдмету и уровню выбора 1
     */
    public List<TaskEntity> getTasksBySubjectAndLevel1 (SubjectEntity subject, String level1){
        return taskRepository.findTaskEntitiesBySubjectAndLevel1(subject, level1);
    }

    /**
     * Функция вывода листа заданий по учебному прдмету и уровню выбора 2 | null, если у предмета нет 2 уровня
     *
     * @param subject - учебный предмет
     * @param level1 - верхний уровень темы
     * @param level2 - уровень темы 2
     * @return Лист заданий по учебному прдмету и уровню выбора 2 | null, если у предмета нет 2 уровня
     */
    public List<TaskEntity> getTasksBySubjectAndLevel2 (SubjectEntity subject, String level1, String level2){
        if(subject.getCountLevel() > 1)
            return taskRepository.findTaskEntitiesBySubjectAndLevel1AndLevel2(subject, level1, level2);
        else
            return null;
    }

    public List<TaskEntity> getTasksBySubjectAndLevel2 (SubjectEntity subject, String level2){
        if(subject.getCountLevel() > 1)
            return taskRepository.findTaskEntitiesBySubjectAndLevel2(subject, level2);
        else
            return null;
    }

    public List<TaskEntity> getAllTasks(){
        return taskRepository.findAll();
    }

    public List<TaskEntity> getSelectionTask(Map<String, Integer> choices, SubjectEntity subject){
        List<TaskEntity> tasks = new ArrayList<>();
        for(Map.Entry<String, Integer> pair : choices.entrySet()){
            List<TaskEntity> t1 ;
            List<TaskEntity> t2 ;
            t1 = taskRepository.findTaskEntitiesBySubjectAndLevel1(subject, pair.getKey());
            if(t1 != null && t1.size() < pair.getValue()){
                throw new CustomException("Not enough tasks");
            }
            else {
                if (t1 == null) {
                    t2 = taskRepository.findTaskEntitiesBySubjectAndLevel2(subject, pair.getKey());
                    if (t2 == null || t2.size() < pair.getValue())
                        throw new CustomException("Not enough tasks");
                    for (int i = 0; i < pair.getValue(); i++) {
                        tasks.add(t2.get(i));
                    }
                } else {
                    for (int i = 0; i < pair.getValue(); i++) {
                        tasks.add(t1.get(i));
                    }
                }
            }
        }
        return tasks;
    }
}