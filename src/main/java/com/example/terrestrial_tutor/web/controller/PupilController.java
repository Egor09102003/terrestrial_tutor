package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.payload.request.AddSubjectRequest;
import com.example.terrestrial_tutor.service.HomeworkService;
import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TutorService;
import com.google.gson.Gson;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


/**
 * Контроллер для работы с учеником
 */
@RequiredArgsConstructor
@Controller
@Api
public class PupilController {

    @Autowired
    @NonNull
    PupilService pupilService;
    @Autowired
    @NonNull
    TutorService tutorService;
    @Autowired
    @NonNull
    HomeworkService homeworkService;
    @Autowired
    private PupilFacade pupilFacade;
    @Autowired
    private SubjectService subjectService;

    /*@PostMapping("/pupil/add")
    public ResponseEntity<PupilEntity> addPupil(@RequestBody PupilEntity pupil) {
        return new ResponseEntity<>(pupilService.addNewPupil(pupil), HttpStatus.OK);
    }*/

    /**
     * Поиск ученика по id
     *
     * @param id id ученика
     * @return ученик
     */
    @GetMapping("/pupil/find")
    public ResponseEntity<PupilEntity> findPupilById(@RequestHeader Long id) {
        return new ResponseEntity<>(pupilService.findPupilById(id), HttpStatus.OK);
    }

    /**
     * Поиск ученика по id
     *
     * @param id id ученика
     * @return ученик
     */
    @GetMapping("/pupil/{id}")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<PupilEntity> getPupilById(@PathVariable Long id) {
        return new ResponseEntity<>(pupilService.findPupilById(id), HttpStatus.OK);
    }

    @GetMapping("/pupil")
    @Secured("hasAnyRole({'PUPIL'})")
    public ResponseEntity<PupilDTO> getCurrentPupil(Principal principal) {
        PupilDTO pupilDTO = pupilFacade.pupilToPupilDTO(pupilService.getCurrentPupil(principal));
        return new ResponseEntity<PupilDTO>(pupilDTO, HttpStatus.OK);
    }

    @GetMapping("/pupil/all")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<List<PupilDTO>> getAllPupils() {
        List<PupilEntity> pupils = pupilService.findAllPupils();
        List<PupilDTO> pupilsDTO = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            pupilsDTO.add(pupilFacade.pupilToPupilDTO(pupil));
        }
        return new ResponseEntity<>(pupilsDTO, HttpStatus.OK);
    }

    @PostMapping("/pupil/add/subjects")
    @Secured("hasAnyRole({'ADMIN'})")
    public ResponseEntity<List<PupilDTO>> addSubjects(@RequestBody AddSubjectRequest addSubjectRequest) {
        String subject = addSubjectRequest.getSubject();
        List<Long> ids = addSubjectRequest.getIds();
        List<PupilEntity> pupils = pupilService.findPupilsByIds(ids);
        List<PupilDTO> pupilsDTO = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            SubjectEntity currentSubject = subjectService.findSubjectByName(subject);
            if (currentSubject != null && !pupil.getSubjects().contains(currentSubject)) {
                pupil.getSubjects().add(currentSubject);
                currentSubject.getPupils().add(pupil);
                subjectService.updateSubject(currentSubject);
            } else {
                throw new EntityExistsException();
            }
            pupilService.updatePupil(pupil);
            pupilsDTO.add(pupilFacade.pupilToPupilDTO(pupil));
        }

        return new ResponseEntity<>(pupilsDTO, HttpStatus.OK);
    }

    @GetMapping("/pupils")
    public ResponseEntity<List<PupilDTO>> getPupilByIds(@RequestParam List<Long> pupilIds) {
        List<PupilEntity> pupils = pupilService.findPupilsByIds(pupilIds);
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PupilDTO> pupilDTOs = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            for (TutorEntity pupilsTutor : pupil.getTutors()) {
                if (pupilsTutor.getId().equals(tutor.getId())) {
                    pupilDTOs.add(pupilFacade.pupilToPupilDTO(pupil));
                    break;
                }
            }
        }
        return new ResponseEntity<>(pupilDTOs, HttpStatus.OK);
    }
    
}
