package com.example.terrestrial_tutor.service;

import java.util.List;

import com.example.terrestrial_tutor.entity.EnrollEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;

/**
 * User enrolls service
 */
public interface EnrollService {

    /**
     * Save multiple enrolls
     * 
     * @param subject enroll subject
     * @param tutor enroll tutor
     * @param pupils enroll pupils
     * @return saved enrolls
     */
    List<EnrollEntity> saveAll(SubjectEntity subject, TutorEntity tutor, List<PupilEntity> pupils);
    
}
