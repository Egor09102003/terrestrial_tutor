package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.exceptions.UserExistException;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.repository.SubjectRepository;
import com.example.terrestrial_tutor.repository.TutorRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.TutorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorServiceImpl implements TutorService {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    @NonNull
    TutorRepository tutorRepository;
    @Autowired
    @NonNull
    PupilRepository pupilRepository;
    @Autowired
    @NonNull
    SubjectRepository subjectRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TutorEntity addNewTutor(RegistrationRequest userIn) {
        TutorEntity tutor = new TutorEntity();
        tutor.setEmail(userIn.getEmail());
        tutor.setName(userIn.getName());
        tutor.setSurname(userIn.getSurname());
        tutor.setPatronymic(userIn.getPatronymic());
        tutor.setUsername(userIn.getEmail());
        tutor.setPassword(passwordEncoder.encode(userIn.getPassword()));
        tutor.setRole(userIn.getRole());

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            if (tutorRepository.findTutorEntityByUsername(userIn.getEmail()) != null
                    || pupilRepository.findPupilEntityByUsername(userIn.getEmail()) != null) {
                throw new UserExistException("Login " + tutor.getUsername() + "already exist");
            } else {
                return tutorRepository.save(tutor);
            }
        } catch (Exception ex) {
            LOG.error("Error during registration");
            throw new UserExistException("The Login " + tutor.getUsername() + "already exist");
        }
    }

    public void deleteTutorById(Long id) {
        tutorRepository.deleteById(id);
    }

    @Override
    public List<SubjectEntity> findTutorSubjectsByTutorId(Long id) {
        List<TutorEntity> tutor = new ArrayList<>();
        tutor.add(tutorRepository.findTutorEntityById(id));
        return subjectRepository.findSubjectEntitiesByTutorsIn(tutor);
    }

    public TutorEntity verifyTutor(Long id) {
        TutorEntity tutor = tutorRepository.findTutorEntityById(id);
        tutor.setVerification(true);
        return tutorRepository.save(tutor);
    }

    public TutorEntity updateTutor(TutorEntity tutor) {
        return tutorRepository.save(tutor);
    }

    @Override
    public TutorEntity addTutorSubject(TutorEntity tutor, SubjectEntity subject) {
        try {
            List<SubjectEntity> subjects = tutor.getSubjects();
            List<TutorEntity> tutors = subject.getTutors();
            if (subjects.contains(subject)) {
                LOG.error("SUBJECT ALREADY EXISTS");
                throw new RuntimeException("Subject already exists");
            }
            subjects.add(subject);
            tutor.setSubjects(subjects);
            tutors.add(tutor);
            subject.setTutors(tutors);
            subjectRepository.save(subject);
            return tutorRepository.save(tutor);
        } catch (Exception ex) {
            LOG.error("ERROR DURING ADDING SUBJECT");
            throw new RuntimeException("Error during adding subject");
        }
    }

    public TutorEntity findTutorById(Long id) {
        return tutorRepository.findTutorEntityById(id);
    }

    public List<TutorEntity> findTutorsWithoutSubject(String subject) {
        List<TutorEntity> tutors = tutorRepository.findAll();
        List<TutorEntity> filtredTutors = new ArrayList<>();
        for (TutorEntity tutorEntity : tutors) {
            List<String> subjects = tutorEntity.getSubjects().stream().map(SubjectEntity::getName).toList();
            if (!subjects.contains(subject)) {
                filtredTutors.add(tutorEntity);
            }
        }
        return filtredTutors;
    }

    public List<TutorEntity> getAllTutors() {
        return this.tutorRepository.findAll();
    }

    public List<TutorEntity> getTutorByIds(List<Long> tutorIds) {
        return tutorRepository.findAllById(tutorIds);
    }
}
