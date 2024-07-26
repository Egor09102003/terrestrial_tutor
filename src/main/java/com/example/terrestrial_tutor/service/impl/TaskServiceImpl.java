package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.dto.facade.TaskFacade;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.example.terrestrial_tutor.exceptions.CustomException;
import com.example.terrestrial_tutor.repository.TaskRepository;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.terrestrial_tutor.entity.enums.TaskCheckingType.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SubjectService subjectService;
    @Autowired
    private TaskFacade taskFacade;

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

    @Override
    public List<TaskEntity> getTasksBySubject(SubjectEntity subject) {
        return taskRepository.findTaskEntitiesBySubject(subject);
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

    @Override
    public TaskEntity getTaskById(Long id) {
        return taskRepository.findTaskEntityById(id);
    }

    @Override
    public TaskEntity save(TaskEntity task) {
        return taskRepository.save(task);
    }

    public List<TaskEntity> getSelectionTask(Map<String, Integer> choices, SubjectEntity subject){
        List<TaskEntity> tasks = new ArrayList<>();
        for(Map.Entry<String, Integer> pair : choices.entrySet()){ // идем по запросу
            List<TaskEntity> t1 ;
            List<TaskEntity> t2 ;
            t1 = taskRepository.findTaskEntitiesBySubjectAndLevel1(subject, pair.getKey()); // ищем в бд нужные задания
            if(t1 != null && t1.size() < pair.getValue()){ // Если мы нашли задания по этой теме, но их не хватает
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

    public TaskEntity addNewTask(TaskDTO dto, SupportEntity support) {
        TaskEntity task = taskFacade.taskDTOToTask(dto, support);
        if (task.getId() != null && dto.getId() != 0) {
            TaskEntity savedTask = taskRepository.findTaskEntityById(dto.getId());
            savedTask.setTaskText("test");
            return taskRepository.saveAndFlush(task);
        }

        return taskRepository.saveAndFlush(task);
    }

    public String toStringName(TaskCheckingType type) {
        return switch (type) {
            case AUTO -> "авто";
            case INSTANCE -> "моментальная";
            case MANUALLY -> "ручная";
        };
    }

    public TaskCheckingType toType(String type) {
        return switch (type) {
            case "авто" -> AUTO;
            case "моментальная" -> INSTANCE;
            case "ручная" -> MANUALLY;
            default -> null;
        };
    }
}
