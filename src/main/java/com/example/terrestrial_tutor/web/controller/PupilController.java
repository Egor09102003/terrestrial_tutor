package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.EnrollDTO;
import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.dto.facade.EnrollFacade;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.dto.facade.TutorListFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.payload.request.AddSubjectRequest;
import com.example.terrestrial_tutor.service.EnrollService;
import com.example.terrestrial_tutor.service.HomeworkService;
import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TutorService;
import com.google.gson.Gson;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




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
    @Autowired
    private TutorListFacade tutorListFacade;
    @Autowired
    EnrollService enrollService;
    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);

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

    @GetMapping("/pupils/check/list/{homeworkId}")
    public ResponseEntity<List<PupilDTO>> getPupilByIds(@PathVariable Long homeworkId, @RequestParam List<Long> pupilIds) {
        List<PupilEntity> pupils = pupilService.findPupilsByIds(pupilIds);
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PupilDTO> pupilDTOs = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            try {
                AttemptEntity bestAttempt = null;
                int lastAttemptNumber = 1;
                for (AttemptEntity attempt: pupil.getAnswers()) {
                    HomeworkEntity homework = attempt.getHomework();
                    HomeworkAnswersDTO test = attempt.getAnswers();
                    if (homework != null && homework.getId().equals(homeworkId)
                        && !attempt.getAnswers().getAnswersStatuses().isEmpty()
                        && attempt.getAttemptNumber() != -1
                        && attempt.getStatus() == HomeworkStatus.FINISHED
                    )
                    {
                        if (bestAttempt == null || attempt.getAttemptPoints() > bestAttempt.getAttemptPoints()) {
                            bestAttempt = attempt;
                        }
                        if (attempt.getAttemptNumber() > lastAttemptNumber) {
                            lastAttemptNumber = attempt.getAttemptNumber();
                        }
                    }
                }

                if (bestAttempt != null && enrollService.checkEnrollment(pupil, bestAttempt.getHomework().getSubject(), tutor)) {
                    PupilDTO pupilDTO = pupilFacade.pupilToPupilDTO(pupil);
                    pupilDTO.setAttempt(bestAttempt.getAnswers());
                    pupilDTO.setLastAttemptNumber(lastAttemptNumber);
                    pupilDTOs.add(pupilDTO);
                }
            } catch(Exception e) {
                log.error("Geting pupil failed: " + e.getMessage(), e);
                continue;
            }
        }
        return new ResponseEntity<>(pupilDTOs, HttpStatus.OK);
    }

    @GetMapping("/pupil/{pupilId}/tutors")
    public ResponseEntity<List<TutorListDTO>> getTutors(@PathVariable Long pupilId) {
        try {
            PupilEntity pupil = pupilService.findPupilById(pupilId);
            return new ResponseEntity<>(tutorListFacade.tutorListToDTO(pupil.getTutors()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to get pupil " + pupilId.toString() + " tutors. Error message: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }    
}
