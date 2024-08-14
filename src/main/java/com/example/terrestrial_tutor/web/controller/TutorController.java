package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.SubjectDTO;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.service.HomeworkService;
import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.TutorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для работы с репетиторами
 */
@RequiredArgsConstructor
@Controller
@Api
@Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
public class TutorController {

    @Autowired
    @NonNull
    PupilService pupilService;

    @Autowired
    @NonNull
    HomeworkService homeworkService;
    @Autowired
    @NonNull
    TutorService tutorService;
    @Autowired
    private PupilFacade pupilFacade;

    /**
     * Поиск учеников репетитора по предмету
     *
     * @param subject предмет
     * @param id      id репетитора
     * @return лист учеников
     */
    @GetMapping("/tutor/find/pupils/{subject}/{id}")
    @Secured("TUTOR")
    public ResponseEntity<List<PupilDTO>> getTutorPupilsBySubject(@PathVariable String subject, @PathVariable Long id) {
        List<PupilEntity> pupils = tutorService.findTutorById(id).getPupils();
        List<PupilDTO> pupilsDTO = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            for (SubjectEntity pupilSubject : pupil.getSubjects()) {
                if (pupilSubject.getName().equals(subject)) {
                    pupilsDTO.add(pupilFacade.pupilToPupilDTO(pupil));
                }
            }
        }
        return new ResponseEntity<>(pupilsDTO, HttpStatus.OK);
    }

    /**
     * Поиск предметов репетитора
     *
     * @return лист предметов
     */
    @GetMapping("/tutor/subjects")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<List<SubjectDTO>> getTutorPupilsBySubject() {
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SubjectEntity> subjects = tutorService.findTutorSubjectsByTutorId(tutor.getId());
        List<SubjectDTO> subjectsDTO = new ArrayList<>();
        for (SubjectEntity subject : subjects) {
            SubjectDTO subjectDTO = new SubjectDTO();
            subjectDTO.setId(subject.getId());
            subjectDTO.setSubjectName(subject.getName());
            subjectsDTO.add(subjectDTO);
        }
        return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);
    }

    /**
     * Поиск всех дз репетитора
     *
     * @return лист дз
     */
    @GetMapping("/tutor/homework/all")
    public ResponseEntity<List<HomeworkEntity>> getAllHomework() {
        return new ResponseEntity<>(homeworkService.getAllHomeworksTutor(), HttpStatus.OK);
    }

}
