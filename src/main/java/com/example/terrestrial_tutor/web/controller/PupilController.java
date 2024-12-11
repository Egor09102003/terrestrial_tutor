package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TutorDTO;
import com.example.terrestrial_tutor.dto.facade.PupilMapper;
import com.example.terrestrial_tutor.dto.facade.TutorMapper;
import com.example.terrestrial_tutor.entity.PupilEntity;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;





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
    public ResponseEntity<?> getCurrentPupil() {
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
    
}
