package com.example.terrestrial_tutor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Enroll DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class EnrollDTO {
    Long id;
    String subject;
    TutorListDTO tutor;
    PupilDTO pupil;
}
