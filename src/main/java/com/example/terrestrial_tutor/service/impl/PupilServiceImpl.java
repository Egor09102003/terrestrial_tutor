package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.exceptions.UserExistException;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.repository.TutorRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.PupilService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PupilServiceImpl implements PupilService {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    @NonNull
    TutorRepository tutorRepository;
    @Autowired
    @NonNull
    PupilRepository pupilRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PupilEntity addNewPupil(RegistrationRequest userIn) {
        PupilEntity pupil = new PupilEntity();
        pupil.setEmail(userIn.getEmail());
        pupil.setName(userIn.getName());
        pupil.setSurname(userIn.getSurname());
        pupil.setPatronymic(userIn.getPatronymic());
        pupil.setUsername(userIn.getEmail());
        pupil.setPassword(passwordEncoder.encode(userIn.getPassword()));
        pupil.setRole(userIn.getRole());

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            if (tutorRepository.findTutorEntityByUsername(userIn.getEmail()) != null
                    || pupilRepository.findPupilEntityByUsername(userIn.getEmail()) != null) {
                throw new UserExistException("Login " + pupil.getUsername() + "already exist");
            } else {
                return pupilRepository.save(pupil);
            }
        } catch (Exception ex) {
            LOG.error("Error during registration");
            throw new UserExistException("The Login " + pupil.getUsername() + "already exist");
        }
    }

    public PupilEntity getCurrentPupil(Principal principal) {
        String username = principal.getName();
        try {
            return pupilRepository.findPupilEntityByUsername(username);
        } catch (Exception ex) {
            throw new UserExistException("Login " + username + "not found");
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

    public List<PupilEntity> getByIds(Iterable<Long> ids) {
        return pupilRepository.findAllById(ids);
    }
}
