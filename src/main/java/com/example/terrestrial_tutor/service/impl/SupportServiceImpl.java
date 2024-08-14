package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.exceptions.UserExistException;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.repository.SupportRepository;
import com.example.terrestrial_tutor.repository.TutorRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.SupportService;
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
public class SupportServiceImpl implements SupportService {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    @NonNull
    TutorRepository tutorRepository;
    @Autowired
    @NonNull
    PupilRepository pupilRepository;
    @Autowired
    @NonNull
    SupportRepository supportRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SupportEntity addNewSupport(RegistrationRequest userIn) {
        SupportEntity support = new SupportEntity();
        support.setEmail(userIn.getEmail());
        support.setName(userIn.getName());
        support.setSurname(userIn.getSurname());
        support.setPatronymic(userIn.getPatronymic());
        support.setUsername(userIn.getEmail());
        support.setPassword(passwordEncoder.encode(userIn.getPassword()));
        support.setRole(userIn.getRole());

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            if (tutorRepository.findTutorEntityByUsername(userIn.getEmail()) != null
                    || pupilRepository.findPupilEntityByUsername(userIn.getEmail()) != null
                    || supportRepository.findSupportEntityByUsername(userIn.getEmail()) != null) {
                throw new UserExistException("Login " + support.getUsername() + "already exist");
            } else {
                return supportRepository.save(support);
            }
        } catch (Exception ex) {
            LOG.error("Error during registration");
            throw new UserExistException("The Login " + support.getUsername() + "already exist");
        }
    }

    public SupportEntity getCurrentSupport(Principal principal) {
        String username = principal.getName();
        try {
            return supportRepository.findSupportEntityByUsername(username);
        } catch (Exception ex) {
            throw new UserExistException("Login " + username + "not found");
        }
    }

    public SupportEntity updateSupport(SupportEntity support) {
        return supportRepository.save(support);
    }

    public List<SupportEntity> findSupportsByIds(List<Long> ids) {
        return supportRepository.findAllById(ids);
    }

    public List<SupportEntity> findAllSupports() {
        return supportRepository.findAll();
    }

    public SupportEntity findSupportById(Long id) {
        return supportRepository.findSupportEntityById(id);
    }

    public SupportEntity save(SupportEntity support) {
        return supportRepository.save(support);
    }
}
