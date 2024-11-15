package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.dto.facade.AttemptFacade;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.dto.facade.TutorListFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.service.AttemptService;
import com.example.terrestrial_tutor.service.EnrollmentService;
import com.example.terrestrial_tutor.service.HomeworkService;
import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TutorService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;





/**
 * Контроллер для работы с учеником
 */
@RequiredArgsConstructor
@Controller
@Api
public class PupilController {

    @NonNull
    PupilService pupilService;
    @NonNull
    TutorService tutorService;
    @NonNull
    HomeworkService homeworkService;
    @NonNull
    private PupilFacade pupilFacade;
    @NonNull
    private SubjectService subjectService;
    @NonNull
    private TutorListFacade tutorListFacade;
    @NonNull
    EnrollmentService enrollmentService;
    @NonNull
    AttemptFacade attemptFacade;
    @NonNull
    AttemptService attemptService;
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
    public ResponseEntity<?> getCurrentPupil(Principal principal) {
        try {
            PupilEntity currentPupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            PupilDTO pupilDTO = pupilFacade.pupilToPupilDTO(currentPupil);
            return new ResponseEntity<>(pupilDTO, HttpStatus.OK);
        } catch (ClassCastException e) {
            return new ResponseEntity<>("Invalid current user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/pupils")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<List<PupilDTO>> getAllPupils() {
        List<PupilEntity> pupils = pupilService.findAllPupils();
        List<PupilDTO> pupilsDTO = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            pupilsDTO.add(pupilFacade.pupilToPupilDTO(pupil));
        }
        return new ResponseEntity<>(pupilsDTO, HttpStatus.OK);
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

    @PostMapping("/pupil/attempt")
    public ResponseEntity<?> saveAttemptAnswers(@RequestParam Long homeworkId, @RequestBody HashMap<Long, String> answers) {
        try {
            PupilEntity pupil;
            pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AttemptEntity currentAttempt = attemptService.getLastActiveAttempt(homeworkId, pupil);
            currentAttempt = attemptService.saveAnswers(currentAttempt, answers);
            return new ResponseEntity<>(attemptFacade.attemptToAttemptDTO(currentAttempt), HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); 
        }
    }

    @GetMapping("/pupil/attempt")
    public ResponseEntity<?> getActiveAttempt(@RequestParam Long homeworkId) {
        try {
            PupilEntity pupil;
            pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AttemptEntity attempt = attemptService.getLastActiveAttempt(homeworkId, pupil);
            return new ResponseEntity<>(attemptFacade.attemptToAttemptDTO(attempt), HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); 
        }
        
    }

    @GetMapping("/pupil/attempt/finish")
    public ResponseEntity<?> getFinishedAttempt(@RequestParam(required = false) Optional<Integer> attemptNumber, 
        @RequestParam Long homeworkId) 
    {
        try {
            PupilEntity pupil;
            pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AttemptEntity attempt = attemptService.getFinishedAttempt(attemptNumber, homeworkId, pupil);
            return new ResponseEntity<>(attemptFacade.attemptToAttemptDTO(attempt), HttpStatus.OK);
        } catch (ClassCastException e) {
           return new ResponseEntity<>("Invalid user", HttpStatus.UNPROCESSABLE_ENTITY); 
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Attempt doesnt exists", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/pupil/attempt/finish")
    public ResponseEntity<?> finishAttempt(@RequestParam Long homeworkId, @RequestBody HashMap<Long, String> answers) {
        try {
            PupilEntity pupil;
            pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AttemptEntity currentAttempt = attemptService.getLastActiveAttempt(homeworkId, pupil);
            currentAttempt = attemptService.finishAttempt(currentAttempt, answers);
            return new ResponseEntity<>(attemptFacade.attemptToAttemptDTO(currentAttempt), HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); 
        }
    }
    
    
    
}
