package com.example.terrestrial_tutor.dto.facade;

import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.EnrollmentDTO;
import com.example.terrestrial_tutor.entity.EnrollmentEntity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Enrollment DTO conversion
 */
@Component
@AllArgsConstructor
public class EnrollmentFacade {

    @NonNull 
    TutorListFacade tutorListFacade;
    @NonNull
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
