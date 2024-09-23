package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terrestrial_tutor.entity.EnrollEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

public interface EnrollRepository extends JpaRepository<EnrollEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO enrolls(pupil, tutor, subject) VALUES (:pupil, :tutor, :subject) ON CONFLICT ON CONSTRAINT uniq_record DO NOTHING", nativeQuery = true)
    public void saveIfNotExist(
            @Param("pupil") Long pupils,
            @Param("tutor") Long tutorId,
            @Param("subject") Long subjectId
    );

    EnrollEntity findFirstByPupilAndTutorAndSubject(PupilEntity pupil, TutorEntity tutor, SubjectEntity subject);
}
