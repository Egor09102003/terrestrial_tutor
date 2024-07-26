package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.User;
import com.example.terrestrial_tutor.repository.PupilRepository;
import com.example.terrestrial_tutor.service.PupilDetailsService;
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
public class PupilDetailsServiceImpl implements PupilDetailsService {

    @Autowired
    PupilRepository pupilRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        PupilEntity pupil = pupilRepository.findPupilEntityByUsername(username);

        if (pupil == null) {
            throw new UsernameNotFoundException("Pupil not found with email:" + username);
        }

        return build(pupil);
    }

    public PupilEntity loadPupilById(Long id) {
        return pupilRepository.findPupilEntityById(id);
    }

    public static PupilEntity build(PupilEntity pupil) {
        GrantedAuthority authorities = new SimpleGrantedAuthority(pupil.getRole().name());
        return new PupilEntity(pupil.getId(),
                pupil.getEmail(),
                pupil.getName(),
                pupil.getSurname(),
                pupil.getPatronymic(),
                pupil.getEmail(),
                pupil.getPassword(),
                pupil.getRole(),
                pupil.getVerification(),
                authorities);
    }
}
