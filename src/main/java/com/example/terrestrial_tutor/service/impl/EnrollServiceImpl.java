package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.EnrollEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.repository.EnrollRepository;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.service.EnrollService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class EnrollServiceImpl implements EnrollService {

    @Autowired
    EnrollRepository enrollRepository;
    @Autowired
    PupilRepository pupilRepository;

    public List<EnrollEntity> saveAll(SubjectEntity subject, TutorEntity tutor, List<PupilEntity> pupils) {
        List<EnrollEntity> enrolls = new ArrayList<>();
        for (PupilEntity pupil : pupils) {
            EnrollEntity enroll = new EnrollEntity();
            enroll.setPupil(pupil);
            enroll.setSubject(subject);
            enroll.setTutor(tutor);
            enrolls.add(enroll);
        }
        return enrollRepository.saveAll(enrolls);
    }
}
