package com.example.terrestrial_tutor.dto.facade;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.terrestrial_tutor.dto.EnrollDTO;
import com.example.terrestrial_tutor.entity.EnrollEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;

/**
 * Enroll DTO conversion
 */
@Component
public class EnrollFacade {

    @Autowired 
    TutorListFacade tutorListFacade;
    @Autowired 
    PupilFacade pupilFacade;

    public EnrollDTO enrollToEnrollDTO(EnrollEntity enroll) {
        return new EnrollDTO(
            enroll.getId(),
            enroll.getSubject().getName(),
            tutorListFacade.tutorToTutorDTO(enroll.getTutor()),
            pupilFacade.pupilToPupilDTO(enroll.getPupil())
        );
    }
    
}
