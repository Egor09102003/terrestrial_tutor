package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.dto.facade.TaskMapper;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.payload.response.TasksResponse;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TaskService;
import com.example.terrestrial_tutor.service.UploadFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.*;
import org.springframework.web.bind.annotation.RequestParam;


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
    TaskMapper taskMapper;

    /**
     * Поиск всех заданий
     *
     * @return все задания
     */
    @GetMapping("/tasks/all")
    public ResponseEntity<TasksResponse> getAllTasks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String level1,
            @RequestParam(required = false) String level2,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String subject
    ) {
        Page<TaskEntity> tasksList = taskService.getAllTasks(
                Optional.of(page - 1),
                Optional.ofNullable(size),
                Optional.ofNullable(name),
                Optional.ofNullable(level1),
                Optional.ofNullable(level2),
                Optional.ofNullable(id),
                Optional.ofNullable(subjectService.findSubjectByName(subject))
        );
        TasksResponse tasksResponse = new TasksResponse(tasksList.getTotalElements(), tasksList.getContent().stream().map(task -> taskMapper.taskToTaskDTO(task)).toList());
        return new ResponseEntity<>(tasksResponse, HttpStatus.OK);
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
        return new ResponseEntity<>(taskMapper.taskToTaskDTO(task), HttpStatus.OK);
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
     * @param fileName - Set of the file names
     */
    @GetMapping("/tasks/files/{fileName}")
    public ResponseEntity<byte[]> getTaskFile(@PathVariable String fileName) throws IOException {
        return new ResponseEntity<>(uploadFilesService.getFilesByPaths(fileName), HttpStatus.OK);
    }

    public List<TaskDTO> getSortedDto(List<TaskEntity> tasksList) {
        tasksList.sort(Comparator.comparingLong(TaskEntity::getId));
        List<TaskDTO> tasksDTO = new ArrayList<>();
        for (TaskEntity task : tasksList) {
            tasksDTO.add(taskMapper.taskToTaskDTO(task));
        }
        return tasksDTO;
    }

    @DeleteMapping("/tasks/{id}/delete")
    public ResponseEntity<Long> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getTaskByIds(@RequestParam(required = false) List<Long> taskIds) {
        List<TaskEntity> tasks;
        if (taskIds == null) {
            tasks = new ArrayList<>();
        } else {
            tasks = taskService.getByIds(taskIds);
        }
        List<TaskDTO> taskDTOs = tasks.stream().map(task -> taskMapper.taskToTaskDTO(task)).toList();
        return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
    }
    

}
