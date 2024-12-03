package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.exceptions.UserExistException;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.repository.TutorRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.AdminService;
import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.TaskService;
import com.example.terrestrial_tutor.service.TutorService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PupilServiceImpl implements PupilService {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @NonNull
    TutorRepository tutorRepository;
    @NonNull
    PupilRepository pupilRepository;
    @NonNull
    TaskService taskService;
    @NonNull
    TutorService tutorService;
    @NonNull
    AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;

    public PupilEntity createPupil(RegistrationRequest registrationRequest) {
        PupilEntity pupil = new PupilEntity();
        pupil.setEmail(registrationRequest.getEmail());
        pupil.setName(registrationRequest.getName());
        pupil.setSurname(registrationRequest.getSurname());
        pupil.setPatronymic(registrationRequest.getPatronymic());
        pupil.setUsername(registrationRequest.getEmail());
        pupil.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        pupil.setRole(registrationRequest.getRole());

        if (tutorService.findTutorByUsername(pupil.getUsername()) != null ||
            adminService.findAdminByUsername(pupil.getUsername()) != null ||
            findPupilByUsername(pupil.getUsername()) != null) 
        {
            throw new UserExistException("Login " + pupil.getUsername() + "already exist");
        } else {
            return pupilRepository.save(pupil);
        }
    }

    public PupilEntity verifyPupil(Long id) {
        PupilEntity pupil = pupilRepository.findPupilEntityById(id);
        pupil.setVerification(true);
        return pupilRepository.save(pupil);
    }

    public PupilEntity updatePupil(PupilEntity pupil) {
        return pupilRepository.save(pupil);
    }

    public List<PupilEntity> findPupilsByIds(List<Long> ids) {
        return pupilRepository.findAllById(ids);
    }

    public List<PupilEntity> findAllPupils() {
        return pupilRepository.findAll();
    }

    public PupilEntity findPupilById(Long id) {
        return pupilRepository.findPupilEntityById(id);
    }

    public Set<PupilEntity> getByTutor(Long tutorId) {
        return pupilRepository.findByTutor(tutorId);
    }

    public Set<PupilEntity> getByTutorAndSubject(Long tutorId, Long subjectId) {
        return pupilRepository.findByTutorAndSubject(tutorId, subjectId);
    }

    public List<AttemptEntity> getAttemptsByHomework(PupilEntity pupil, HomeworkEntity homework) {
        List<AttemptEntity> attemptsByHomework = new ArrayList<>();
        for (AttemptEntity attempt : pupil.getHomeworkAttempts()) {
            if (!attempt.getStatus().equals(HomeworkStatus.FINISHED) && attempt.getHomework().equals(homework)) {
                attemptsByHomework.add(attempt);
            }
        }
        return attemptsByHomework;
    }

    public PupilEntity findPupilByUsername(String username) {
        return pupilRepository.findPupilEntityByUsername(username);
    }

    public List<PupilEntity> findByTutorAndHomework(TutorEntity tutor, HomeworkEntity homework) {
        return pupilRepository.findDistinctByHomeworkListContainsAndEnrollmentsTutorAndEnrollmentsSubject(homework, tutor, homework.getSubject());
    }
}
