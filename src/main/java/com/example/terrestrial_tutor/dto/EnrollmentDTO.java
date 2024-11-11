package com.example.terrestrial_tutor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Enrollment DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class EnrollmentDTO {
    Long id;
    String subject;
    TutorListDTO tutor;
    PupilDTO pupil;
}
