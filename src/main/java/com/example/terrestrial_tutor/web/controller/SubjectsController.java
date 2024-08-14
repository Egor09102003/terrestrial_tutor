package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.SubjectDTO;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для работы с предметами
 */
@RequiredArgsConstructor
@Controller
@Api
public class SubjectsController {

    @Autowired
    private SubjectService subjectService;

    /**
     * Поиск всех предметов
     *
     * @return все предметы
     */
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> findAllSubjects() {
        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        List<SubjectDTO> subjectsDTO = new ArrayList<>();
        for (SubjectEntity subject : subjects) {
            SubjectDTO subjectDTO = new SubjectDTO();
            subjectDTO.setSubjectName(subject.getName());
            subjectDTO.setId(subject.getId());
            subjectsDTO.add(subjectDTO);
        }
        return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);
    }

}
