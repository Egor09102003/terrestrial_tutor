package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.AdminEntity;
import com.example.terrestrial_tutor.exceptions.UserExistException;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;
import com.example.terrestrial_tutor.repository.AdminRepository;
import com.example.terrestrial_tutor.security.JWTAuthenticationFilter;
import com.example.terrestrial_tutor.service.AdminService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    @NonNull
    AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminEntity addNewAdmin(RegistrationRequest userIn) {
        AdminEntity admin = new AdminEntity();
        admin.setEmail(userIn.getEmail());
        admin.setName(userIn.getName());
        admin.setSurname(userIn.getSurname());
        admin.setPatronymic(userIn.getPatronymic());
        admin.setUsername(userIn.getEmail());
        admin.setPassword(passwordEncoder.encode(userIn.getPassword()));
        admin.setRole(userIn.getRole());

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            if (adminRepository.findAdminEntityByUsername(admin.getUsername()) != null) {
                throw new UserExistException("Login " + admin.getUsername() + "already exist");
            }
            return adminRepository.save(admin);
        } catch (Exception ex) {
            LOG.error("Error during registration");
            throw new UserExistException("The Login " + admin.getUsername() + "already exist");
        }
    }

    public AdminEntity findAdminById(Long id) {
        return adminRepository.findAdminEntityById(id);
    }

    public AdminEntity findAdminByUsername(String username) {
        return adminRepository.findAdminEntityByUsername(username);
    }
}
