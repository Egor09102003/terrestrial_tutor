package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.facade.AttemptMapper;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.exceptions.AttemptFinishedException;
import com.example.terrestrial_tutor.service.AttemptService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@Api
public class AttemptController {

    @NonNull
    AttemptMapper attemptMapper;
    @NonNull
    AttemptService attemptService;
    static final Logger log =
            LoggerFactory.getLogger(AttemptController.class);

    @PostMapping("/attempt")
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

    @GetMapping("/attempt")
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

    @GetMapping("/attempt/finish")
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

    @PostMapping("/attempt/finish")
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
