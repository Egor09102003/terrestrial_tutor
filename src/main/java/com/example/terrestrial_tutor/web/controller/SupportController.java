package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.service.TaskService;
import com.example.terrestrial_tutor.service.UploadFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Контроллер для работы с тех поддержкой
 */
@RequiredArgsConstructor
@Controller
@Api
public class SupportController {

    @Autowired
    TaskService taskService;

    @Autowired
    UploadFilesService uploadFilesService;

    /**
     * Добавление задания в бд
     *
     * @param task тип ответа
     * @return статус операции
     */
    @PostMapping("/support/add/task")
    @Secured("hasAnyRole({'SUPPORT'})")
    public ResponseEntity<Long> addTask(@RequestBody TaskDTO task) {
        SupportEntity support = (SupportEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long taskId = taskService.addNewTask(task, support).getId();
        return new ResponseEntity<>(taskId, HttpStatus.OK);
    }

    @PostMapping("/support/add/file/{id}")
    public HttpStatus addFilesForTask(@PathVariable Long id,
                                                @RequestParam(required = false) Set<MultipartFile> files
    ) throws Exception {
        try {
            TaskEntity task = taskService.getTaskById(id);
            if (files != null) {
                task.getFiles().addAll(uploadFilesService.uploadFiles(files));
            }
            taskService.save(task);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return HttpStatus.OK;
    }
}
