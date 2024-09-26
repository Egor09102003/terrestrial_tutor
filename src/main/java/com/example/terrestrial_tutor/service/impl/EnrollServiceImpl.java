package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.entity.EnrollEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.repository.EnrollRepository;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.service.EnrollService;

import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TutorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class EnrollServiceImpl implements EnrollService {

    @Autowired
    EnrollRepository enrollRepository;
    @Autowired
    PupilRepository pupilRepository;
    @Autowired
    PupilService pupilService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    TutorService tutorService;

    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);


    @Transactional
    public List<PupilEntity> saveAll(Long subject, Long tutor, List<Long> pupilIds) {
        List<PupilEntity> currentPupils = new ArrayList<>(tutorService.findTutorById(tutor).getPupilsBySubject(subject));
        List<PupilEntity> updatedCurrentPupils = new ArrayList<PupilEntity>(currentPupils);
        try {
            List<PupilEntity> pupils = pupilService.findPupilsByIds(pupilIds);
            TutorEntity tutorEntity = tutorService.findTutorById(tutor);
            SubjectEntity subjectEntity = subjectService.findSubjectById(subject);
            for (PupilEntity currentPupil : currentPupils) {
                if (!pupils.contains(currentPupil)) {
                    enrollRepository.deleteByPupilAndTutorAndSubject(currentPupil, tutorEntity, subjectEntity);
                    updatedCurrentPupils.remove(currentPupil);
                }
            }
            for (PupilEntity pupil : pupils) {
                if (!updatedCurrentPupils.contains(pupil)) {
                    enrollRepository.saveIfNotExist(pupil.getId(), tutor, subject);
                    updatedCurrentPupils.add(pupil);
                }
            }
        } catch (Exception e) {
            log.error("Failed to save enroll: {}", e.getMessage());
            throw new NoSuchElementException("Failed to save enroll");
        }
        return updatedCurrentPupils;
    }

    public Boolean checkEnrollment(PupilEntity pupil, SubjectEntity subject, TutorEntity tutor) {
        return enrollRepository.findFirstByPupilAndTutorAndSubject(pupil, tutor, subject) != null;
    }
}
