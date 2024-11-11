package com.example.terrestrial_tutor.dto.facade;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.EnrollmentDTO;
import com.example.terrestrial_tutor.entity.EnrollmentEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;

/**
 * Enrollment DTO conversion
 */
@Component
public class EnrollmentFacade {

    @Autowired 
    TutorListFacade tutorListFacade;
    @Autowired 
    PupilFacade pupilFacade;

    public EnrollmentDTO enrollToEnrollDTO(EnrollmentEntity enroll) {
        return new EnrollmentDTO(
            enroll.getId(),
            enroll.getSubject().getName(),
            tutorListFacade.tutorToTutorDTO(enroll.getTutor()),
            pupilFacade.pupilToPupilDTO(enroll.getPupil())
        );
    }
    
}
