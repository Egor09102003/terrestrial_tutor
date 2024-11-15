package com.example.terrestrial_tutor.service;

import java.util.List;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;

/**
 * User enrollments service
 */
public interface EnrollmentService {

    /**
     * Save multiple enrollments
     * 
     * @param subject enrollment subject
     * @param tutor enrollment tutor
     * @param pupils enrollment pupils
     * @return saved enrollments
     */
    List<PupilEntity> saveAll(Long subject, Long tutor, List<Long> pupils);

    Boolean checkEnrollment(PupilEntity pupil, SubjectEntity subject, TutorEntity tutor);
    
}
