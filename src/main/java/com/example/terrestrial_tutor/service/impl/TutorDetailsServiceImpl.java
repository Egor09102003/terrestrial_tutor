package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.entity.User;
import com.example.terrestrial_tutor.repository.TutorRepository;
import com.example.terrestrial_tutor.service.TutorDetailsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class TutorDetailsServiceImpl implements TutorDetailsService {

    @Autowired
    TutorRepository tutorRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        TutorEntity tutor = tutorRepository.findTutorEntityByUsername(username);

        if (tutor == null) {
            throw new UsernameNotFoundException("Tutor not found with email:" + username);
        }

        return build(tutor);
    }

    public TutorEntity loadTutorById(Long id) {
        return tutorRepository.findTutorEntityById(id);
    }

    public static TutorEntity build(TutorEntity tutor) {
        GrantedAuthority authorities = new SimpleGrantedAuthority(tutor.getRole().name());
        return new TutorEntity(tutor.getId(),
                tutor.getEmail(),
                tutor.getName(),
                tutor.getSurname(),
                tutor.getPatronymic(),
                tutor.getEmail(),
                tutor.getPassword(),
                tutor.getRole(),
                tutor.getVerification(),
                authorities);
    }
}
