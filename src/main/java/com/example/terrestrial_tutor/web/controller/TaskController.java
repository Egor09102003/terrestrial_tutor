package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.dto.facade.TaskFacade;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.payload.response.FilesResponse;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TaskService;
import com.example.terrestrial_tutor.service.UploadFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.*;

/**
 * Контроллер дл работы с заданиями
 */
@RequiredArgsConstructor
@Controller
@Api
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    UploadFilesService uploadFilesService;

    @Autowired
    TaskFacade taskFacade;

    /**
     * Поиск всех заданий
     *
     * @return все задания
     */
    @GetMapping("/tasks/all")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskEntity> tasksList = taskService.getAllTasks();
        return new ResponseEntity<>(getSortedDto(tasksList), HttpStatus.OK);
    }

    /**
     * Контроллер для отдачи выборки заданий по предмету и верхней заданной теме
     *
     * @param subject - предмет
     * @param level1  - верхняя тема выборки заданий
     * @return лист выборки заданий
     */
    @GetMapping("/tasks/{subject}/{level1}")
    public ResponseEntity<List<TaskEntity>> getTasksByLevel1(@PathVariable String subject, @PathVariable String level1) {
        SubjectEntity currentSubject = subjectService.findSubjectByName(subject);
        List<TaskEntity> listTasks = taskService.getTasksBySubjectAndLevel1(currentSubject, level1);
        return new ResponseEntity<>(listTasks, HttpStatus.OK);
    }

    /**
     * Поиск заданий по предмету
     *
     * @param subject предмет
     * @return лист заданий
     */
    @GetMapping("/tasks/{subject}")
    public ResponseEntity<List<TaskDTO>> getTasksBySubjectAndHW(@PathVariable String subject) {
        SubjectEntity currentSubject = subjectService.findSubjectByName(subject);
        List<TaskEntity> tasksList = taskService.getTasksBySubject(currentSubject);
        return new ResponseEntity<>(getSortedDto(tasksList), HttpStatus.OK);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<TaskDTO> getTasksById(@PathVariable Long id) {
        TaskEntity task = taskService.getTaskById(id);
        return new ResponseEntity<>(taskFacade.taskToTaskDTO(task), HttpStatus.OK);
    }

    /**
     * Контроллер для отдачи выборки заданий по предмету, верхней теме и подтеме
     *
     * @param subject - предмет
     * @param level1  - верхняя тема выборки задания
     * @param level2  - подтема выборки задания
     * @return лист выборки задания
     */
    @GetMapping("/tasks/{subject}/{level1}/{level2}")
    public ResponseEntity<List<TaskEntity>> getTasksByLevel1AndLevel2(@PathVariable String subject, @PathVariable String level1,
                                                                      @PathVariable String level2) {
        SubjectEntity currentSubject = subjectService.findSubjectByName(subject);
        List<TaskEntity> listTasks = taskService.getTasksBySubjectAndLevel2(currentSubject, level1, level2);
        return new ResponseEntity<>(listTasks, HttpStatus.OK);
    }

    /**
     * Получение файлов задания
     * 
     * @param id
     * @return
     * @throws IOException
     */
    @GetMapping("/tasks/{id}/files")
    public ResponseEntity<Set<FilesResponse>> getTaskFile(@PathVariable Long id) throws IOException {
        TaskEntity task = taskService.getTaskById(id);
        return new ResponseEntity<>(uploadFilesService.getFilesByPaths(task.getFiles()), HttpStatus.OK);
    }

    public List<TaskDTO> getSortedDto(List<TaskEntity> tasksList) {
        tasksList.sort(Comparator.comparingLong(TaskEntity::getId));
        List<TaskDTO> tasksDTO = new ArrayList<>();
        for (TaskEntity task : tasksList) {
            tasksDTO.add(taskFacade.taskToTaskDTO(task));
        }
        return tasksDTO;
    }

    @DeleteMapping("/tasks/{id}/delete")
    public ResponseEntity<Long> deleteTask(@PathVariable Long id) {
        System.out.println(id);
        System.out.println("id");
        taskService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}
