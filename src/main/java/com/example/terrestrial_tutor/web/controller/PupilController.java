package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.AttemptDTO;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.dto.facade.TutorListFacade;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.payload.request.AddSubjectRequest;
import com.example.terrestrial_tutor.repository.AttemptRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;




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

    @GetMapping("/pupils/check/list/{homeworkId}")
    public ResponseEntity<List<PupilDTO>> getPupilByIds(@PathVariable Long homeworkId, @RequestParam List<Long> pupilIds) {
        List<PupilEntity> pupils = pupilService.findPupilsByIds(pupilIds);
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PupilDTO> pupilDTOs = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            try {
                AttemptEntity bestAttempt = null;
                int lastAttemptNumber = 1;
                for (AttemptEntity attempt: pupil.getHomeworkAttempts()) {
                    HomeworkEntity homework = attempt.getHomework();
                    if (homework != null && homework.getId().equals(homeworkId)
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

                if (bestAttempt != null && enrollmentService.checkEnrollment(pupil, bestAttempt.getHomework().getSubject(), tutor)) {
                    PupilDTO pupilDTO = pupilFacade.pupilToPupilDTO(pupil);
                    pupilDTO.setLastAttemptNumber(lastAttemptNumber);
                    pupilDTOs.add(pupilDTO);
                }
            } catch(Exception e) {
                log.error("Geting pupil failed: {}", e.getMessage(), e);
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

    @PostMapping("/pupil/attempt")
    public ResponseEntity<?> saveAttemptAnswers(@RequestParam Long homeworkId, @RequestBody HashMap<Long, String> answers) {
        try {
            PupilEntity pupil;
            pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AttemptEntity currentAttempt = attemptService.getLastActiveAttempt(homeworkId, pupil);
            currentAttempt = attemptService.saveAnswers(currentAttempt, answers);
            return new ResponseEntity<>(new AttemptDTO(currentAttempt), HttpStatus.OK);
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
            return new ResponseEntity<>(new AttemptDTO(attempt), HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); 
        }
        
    }

    @GetMapping("/pupil/attempt/finished")
    public ResponseEntity<?> getFinishedAttempt(@RequestParam(required = false) Optional<Integer> attemptNumber, 
        @RequestParam Long homeworkId) 
    {
        try {
            PupilEntity pupil;
            pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            AttemptEntity attempt = attemptService.getFinishedAttempt(attemptNumber, homeworkId, pupil);
            return new ResponseEntity<>(new AttemptDTO(attempt), HttpStatus.OK);
        } catch (ClassCastException e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); 
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid attempt number", HttpStatus.BAD_REQUEST);
        }
    }
    
    
}
