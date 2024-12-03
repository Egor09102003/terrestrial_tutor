package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TutorDTO;
import com.example.terrestrial_tutor.dto.facade.AttemptMapper;
import com.example.terrestrial_tutor.dto.facade.PupilMapper;
import com.example.terrestrial_tutor.dto.facade.TutorMapper;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.exceptions.AttemptFinishedException;
import com.example.terrestrial_tutor.service.AttemptService;
import com.example.terrestrial_tutor.service.PupilService;

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
    private PupilMapper pupilMapper;
    @NonNull
    private TutorMapper tutorMapper;
    @NonNull
    AttemptMapper attemptMapper;
    @NonNull
    AttemptService attemptService;
    static final Logger log =
            LoggerFactory.getLogger(PupilController.class);

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
            PupilDTO pupilDTO = pupilMapper.toDTO(currentPupil);
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
            pupilsDTO.add(pupilMapper.toDTO(pupil));
        }
        return new ResponseEntity<>(pupilsDTO, HttpStatus.OK);
    }

    @GetMapping("/pupil/{pupilId}/tutors")
    public ResponseEntity<List<TutorDTO>> getTutors(@PathVariable Long pupilId) {
        try {
            PupilEntity pupil = pupilService.findPupilById(pupilId);
            return new ResponseEntity<>(tutorMapper.tutorListToDTO(pupil.getTutors()), HttpStatus.OK);
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
            return new ResponseEntity<>(attemptMapper.attemptToAttemptDTO(currentAttempt, false), HttpStatus.OK);
        } catch (AttemptFinishedException e) {
          return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>(attemptMapper.attemptToAttemptDTO(attempt, false), HttpStatus.OK);
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
            return new ResponseEntity<>(attemptMapper.attemptToAttemptDTO(attempt, true), HttpStatus.OK);
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
            return new ResponseEntity<>(attemptMapper.attemptToAttemptDTO(currentAttempt, false), HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY); 
        }
    }
    
    
    
}
