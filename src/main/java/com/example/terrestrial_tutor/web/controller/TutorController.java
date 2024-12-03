package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.SubjectDTO;
import com.example.terrestrial_tutor.dto.TutorDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkMapper;
import com.example.terrestrial_tutor.dto.facade.PupilMapper;
import com.example.terrestrial_tutor.dto.facade.TutorMapper;
import com.example.terrestrial_tutor.entity.*;
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

import java.util.*;





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
    private PupilMapper pupilMapper;
    @Autowired
    private HomeworkMapper homeworkMapper;
    @Autowired
    private TutorMapper tutorMapper;
    @Autowired
    @NonNull
    SubjectService subjectService;

    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);

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
        Set<PupilEntity> pupils = tutorService.findTutorById(id).getPupils();
        List<PupilDTO> pupilsDTO = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            for (SubjectEntity pupilSubject : pupil.getSubjects()) {
                if (pupilSubject.getName().equals(subject)) {
                    pupilsDTO.add(pupilMapper.toDTO(pupil));
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
    public ResponseEntity<List<SubjectDTO>> getTutorSubjects() {
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<SubjectEntity> subjects = tutorService.findTutorSubjectsByTutorId(tutor.getId());
        LinkedList<SubjectDTO> subjectsDTO = new LinkedList<>();
        for (SubjectEntity subject : subjects) {
            SubjectDTO subjectDTO = new SubjectDTO();
            subjectDTO.setId(subject.getId());
            subjectDTO.setSubjectName(subject.getName());
            subjectsDTO.add(subjectDTO);
        }
        subjectsDTO.sort(Comparator.comparing(SubjectDTO::getSubjectName));
        return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);
    }

    /**
     * Поиск всех дз репетитора
     *
     * @return лист дз
     */
    @GetMapping("/tutor/homeworks")
    public ResponseEntity<List<HomeworkDTO>> getAllHomework() {
        List<HomeworkDTO> homeworkDTOs = new ArrayList<>();
        List<HomeworkEntity> homeworks = homeworkService.getAllHomeworksTutor();
        for (HomeworkEntity homework : homeworks) {
            homeworkDTOs.add(homeworkMapper.homeworkToHomeworkDTO(homework, true));
        }
        return new ResponseEntity<>(homeworkDTOs, HttpStatus.OK);
    }

    @GetMapping("/tutors")
    public ResponseEntity<List<TutorDTO>> getAllTutors() {
        return new ResponseEntity<>(tutorMapper.tutorListToDTO(tutorService.getAllTutors()), HttpStatus.OK);
    }

    /**
     * Get tutor asigned pupils
     * 
     * @param tutorId tutor id
     * @param subject sunject name
     * @return pils DTOs
     */
    @GetMapping("/tutor/{tutorId}/pupils")
    public ResponseEntity<List<PupilDTO>> getPupils(@PathVariable Long tutorId, @RequestParam Optional<String> subject) {
        try {
            Set<PupilEntity> pupils;
            if (subject.isPresent()) {
                pupils = pupilService.getByTutorAndSubject(tutorId, subjectService.findSubjectByName(subject.get()).getId());
            } else {
                pupils = pupilService.getByTutor(tutorId);
            }
            List<PupilDTO> pupilDTOs = new ArrayList<>();
            for (PupilEntity pupil : pupils) {
                pupilDTOs.add(pupilMapper.toDTO(pupil));
            }
            return new ResponseEntity<>(pupilDTOs, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to get pupils for tutor: " + tutorId.toString() + ". Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

     @GetMapping("/tutor/homework/{homeworkId}/attempts")
     public ResponseEntity<List<PupilDTO>> getAttemptsForChecking(@PathVariable Long homeworkId) {
        TutorEntity tutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HomeworkEntity homework = homeworkService.getHomeworkById(homeworkId);
         List<PupilEntity> pupils = pupilService.findByTutorAndHomework(tutor, homework);
         List<PupilDTO> pupilDTOs = new ArrayList<>();
         for (PupilEntity pupil : pupils) {
             pupilDTOs.add(pupilMapper.toDTOWithAttempt(pupil));
         }

         return new ResponseEntity<>(pupilDTOs, HttpStatus.OK);
     }
}
