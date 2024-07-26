package com.example.terrestrial_tutor.payload.request;

import com.example.terrestrial_tutor.annotations.PasswordMatches;
import com.example.terrestrial_tutor.entity.enums.ERole;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * Запрос регистрации
 */
@Data
@PasswordMatches
public class RegistrationRequest {

    @NotEmpty(message = "Enter your name")
    private String name;
    @NotEmpty(message = "Enter your surname")
    private String surname;
    @NotEmpty(message = "Enter your patronymic")
    private String patronymic;
    @Email(message = "It should be email format")
    @NotBlank(message = "User email is required")
    private String email;
    @NotEmpty(message = "Password is required")
    @Size(min = 8)
    private String password;
    private String confirmPassword;
    private ERole role;

}
