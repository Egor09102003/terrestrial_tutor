package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.SubjectDTO;
import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkFacade;
import com.example.terrestrial_tutor.dto.facade.PupilFacade;
import com.example.terrestrial_tutor.dto.facade.TutorListFacade;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;


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
    @Autowired
    private HomeworkFacade homeworkFacade;
    @Autowired
    private TutorListFacade tutorListFacade;
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
    @GetMapping("/tutor/homeworks")
    public ResponseEntity<List<HomeworkDTO>> getAllHomework() {
        List<HomeworkDTO> homeworkDTOs = new ArrayList<>();
        List<HomeworkEntity> homeworks = homeworkService.getAllHomeworksTutor();
        for (HomeworkEntity homework : homeworks) {
            homeworkDTOs.add(homeworkFacade.homeworkToHomeworkDTO(homework));
        }
        return new ResponseEntity<>(homeworkDTOs, HttpStatus.OK);
    }

    @GetMapping("/tutors")
    public ResponseEntity<List<TutorListDTO>> getAllTutors() {
        return new ResponseEntity<>(tutorListFacade.tutorListToDTO(tutorService.getAllTutors()), HttpStatus.OK);
    }
    
    @GetMapping("/tutor/pupils")
    public ResponseEntity<Set<PupilDTO>> getCurrentPupils(@RequestParam String subject) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            TutorEntity tutor = tutorService.findTutorById(user.getId());
            List<PupilDTO> pupilDTOs = new ArrayList<>();
            for (PupilEntity pupil : tutor.getPupils()) {
                for (SubjectEntity pupilSubject : pupil.getSubjects()) {
                    log.info(pupilSubject.getName());
                    if (pupilSubject.getName().equals(subject)) {
                        pupilDTOs.add(pupilFacade.pupilToPupilDTO(pupil));
                        break;
                    }
                }
            }
            return new ResponseEntity<>(new HashSet<PupilDTO>(pupilDTOs), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
    
}
