package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.dto.facade.EnrollmentFacade;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.dto.facade.TutorListFacade;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Контроллер для администратора
 */
@CrossOrigin
@Api
public class AdminController {

    @Autowired
    PupilService pupilService;
    @Autowired
    TutorService tutorService;
    @Autowired
    AdminService adminService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    TutorListFacade tutorListFacade;
    @Autowired
    private PupilFacade pupilFacade;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentFacade enrollmentFacade;

    /**
     * Поиск репетиторов по заданному предмету
     *
     * @param subject предмет
     * @return лист dto репетиторов
     */
    @GetMapping("/admin/subject/{subject}/find/tutors")
    @Secured("hasAnyRole({'ADMIN'})")
    public ResponseEntity<List<TutorListDTO>> findTutorsBySubject(@PathVariable String subject) {
        return new ResponseEntity<>(tutorListFacade.tutorListToDTO(subjectService.findSubjectTutors(subject)), HttpStatus.OK);
    }

    /**
     * Добавление учеников репетитору по его id
     *
     * @param pupilsIds id учеников
     * @param id        id репетитора
     * @return лист добавленных учеников
     */
    @PostMapping("/admin/tutor/{id}/add/pupils")
    @Secured("hasAnyRole({'ADMIN'})")
    public ResponseEntity<List<PupilDTO>> addPupilsForTutor(@RequestParam String subject, @RequestBody List<Long> pupilsIds, @PathVariable Long id) {
        List<PupilEntity> pupils = pupilService.findPupilsByIds(pupilsIds);
        TutorEntity tutor = tutorService.findTutorById(id);
        SubjectEntity subjectEntity = subjectService.findSubjectByName(subject);
        tutor.getPupils().addAll(pupils);
        tutorService.updateTutor(tutor);
        List<PupilDTO> pupilsDTO = tutor.getPupils()
                .stream()
                .filter(pupil -> pupil.getSubjects().contains(subjectEntity))
                .map(pupil -> pupilFacade.pupilToPupilDTO(pupil))
                .toList();
        return new ResponseEntity<>(pupilsDTO, HttpStatus.OK);
    }

    /**
     * Поиск учеников по предмету
     *
     * @param subject предмет
     * @return лист учеников
     */
    @GetMapping("/admin/find/pupils/new/{subject}")
    @Secured("hasAnyRole({'ADMIN'})")
    public ResponseEntity<List<PupilDTO>> findPupilsWithoutSubject(@PathVariable String subject) {
        List<PupilEntity> allPupils = pupilService.findAllPupils();
        List<PupilDTO> resultPupils = new ArrayList<>();
        for (PupilEntity pupil : allPupils) {
            List<String> pupilSubjects = pupil.getSubjects()
                    .stream()
                    .map(SubjectEntity::getName)
                    .toList();
            if (!pupilSubjects.contains(subject) && pupil.getVerification()) {
                resultPupils.add(pupilFacade.pupilToPupilDTO(pupil));
            }
        }
        return new ResponseEntity<>(resultPupils, HttpStatus.OK);
    }

    /**
     * Добавление предмета репетиторам
     *
     * @param subject  предмет
     * @param tutorIds id репетиторов
     * @return лист репетиторов
     * @throws Exception
     */
    @PostMapping("/admin/tutor/add/subject/{subject}")
    @Secured("hasAnyRole({'ADMIN'})")
    public ResponseEntity<List<TutorListDTO>> addSubjectToTutor(@PathVariable String subject, @RequestBody List<Long> tutorIds) throws Exception {
        List<TutorEntity> tutors = new ArrayList<>();
        for (Long tutorId : tutorIds) {
            tutors.add(tutorService.findTutorById(tutorId));
        }
        try {
            for (TutorEntity tutor : tutors) {
                tutorService.addTutorSubject(tutor, subjectService.findSubjectByName(subject));
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return new ResponseEntity<>(tutorListFacade.tutorListToDTO(subjectService.findSubjectByName(subject).getTutors()), HttpStatus.OK);
    }

    /**
     * Поиск репетиторов по предмету
     *
     * @param subject предмет
     * @return лист репетиторов
     */
    @GetMapping("/admin/find/tutors/new/{subject}")
    @Secured("hasAnyRole({'ADMIN'})")
    public ResponseEntity<List<TutorListDTO>> findTutorsWithoutSubject(@PathVariable String subject) {
        List<TutorEntity> filtredTutors = tutorService.findTutorsWithoutSubject(subject);
        List<TutorListDTO> result = tutorListFacade.tutorListToDTO(filtredTutors);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Enrollment pupil for tutor and subject
     *
     * @param subjectName path variable with subject name
     * @param pupilIds request body with pupil ids
     * @param tutorId path variable with tutor id
     * @return enrolled pupils list
     */
    @PostMapping("/admin/{tutorId}/enrollment/{subjectName}")
    public ResponseEntity<List<PupilDTO>> pupilsEnrollments(@PathVariable String subjectName, @RequestBody List<Long> pupilIds, @PathVariable Long tutorId) {
        SubjectEntity subject = subjectService.findSubjectByName(subjectName);
        try {
            return new ResponseEntity<>(
                    enrollmentService.saveAll(subject.getId(), tutorId, pupilIds)
                            .stream()
                            .map(pupilEntity -> pupilFacade.pupilToPupilDTO(pupilEntity))
                            .toList(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
