package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.User;
import com.example.terrestrial_tutor.repository.SupportRepository;
import com.example.terrestrial_tutor.service.SupportDetailsService;
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
public class SupportDetailsServiceImpl implements SupportDetailsService {

    @Autowired
    SupportRepository supportRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        SupportEntity support = supportRepository.findSupportEntityByUsername(username);

        if (support == null) {
            throw new UsernameNotFoundException("Support not found with email:" + username);
        }

        return build(support);
    }

    public SupportEntity loadSupportById(Long id) {
        return supportRepository.findSupportEntityById(id);
    }

    public static SupportEntity build(SupportEntity support) {
        GrantedAuthority authorities = new SimpleGrantedAuthority(support.getRole().name());
        return new SupportEntity(support.getId(),
                support.getEmail(),
                support.getName(),
                support.getSurname(),
                support.getPatronymic(),
                support.getEmail(),
                support.getPassword(),
                support.getRole(),
                authorities);
    }
}
