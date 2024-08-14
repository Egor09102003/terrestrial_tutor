package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.AdminEntity;
import com.example.terrestrial_tutor.entity.User;
import com.example.terrestrial_tutor.repository.AdminRepository;
import com.example.terrestrial_tutor.service.AdminDetailsService;
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
public class AdminDetailsServiceImpl implements AdminDetailsService {

    @Autowired
    AdminRepository adminRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminEntity admin = adminRepository.findAdminEntityByUsername(username);

        if (admin == null) {
            throw new UsernameNotFoundException("Tutor not found with email:" + username);
        }

        return build(admin);
    }

    public AdminEntity loadAdminById(Long id) {
        return adminRepository.findAdminEntityById(id);
    }

    public static AdminEntity build(AdminEntity admin) {
        GrantedAuthority authorities = new SimpleGrantedAuthority(admin.getRole().name());
        return new AdminEntity(admin.getId(),
                admin.getEmail(),
                admin.getName(),
                admin.getSurname(),
                admin.getPatronymic(),
                admin.getEmail(),
                admin.getPassword(),
                admin.getRole(),
                authorities);
    }
}
