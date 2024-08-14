package com.example.terrestrial_tutor.payload.response;

import com.example.terrestrial_tutor.entity.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Успешный ответ на токен при авторизации
 */
@Data
@AllArgsConstructor
public class JWTTokenSuccessResponse {
    private boolean success;
    private String token;
    private ERole role;
    private Long userId;
}
