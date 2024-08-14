package com.example.terrestrial_tutor.entity;


import com.example.terrestrial_tutor.entity.enums.ERole;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Интерфейс пользователя
 */
public interface User extends UserDetails{
    public Long getId();
    public ERole getRole();
    public String getEmail();
    public Boolean getVerification();
}
