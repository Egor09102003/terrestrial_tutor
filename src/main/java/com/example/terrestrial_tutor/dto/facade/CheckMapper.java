package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.CheckDTO;
import com.example.terrestrial_tutor.entity.CheckEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.exceptions.CustomException;
import com.example.terrestrial_tutor.service.PupilService;
import com.example.terrestrial_tutor.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Класс для перевода CheckEntity в CheckDTO
 */
@Component
public class CheckMapper {

    @Autowired
    PupilService pupilService;
    @Autowired
    TutorService tutorService;

    /**
     * Метод для перевода CheckEntity в CheckDTO
     *
     * @param checkEntity сущность проверки
     * @return сущность DTO
     */

    public CheckDTO checkToCheckDTO(CheckEntity checkEntity) {
        CheckDTO checkDTO = new CheckDTO();
        HashMap<String, String> claims = new HashMap<>();
        Long userId = checkEntity.getCandidateId();

        try {
            if (pupilService.findPupilById(userId) != null) {
                PupilEntity user = pupilService.findPupilById(userId);
                claims.put("username", user.getUsername());
                claims.put("name", user.getName());
                claims.put("surname", user.getSurname());
                claims.put("patronymic", user.getPatronymic());
                claims.put("role", user.getRole().toString());
            } else {
                TutorEntity user = tutorService.findTutorById(userId);
                claims.put("username", user.getUsername());
                claims.put("name", user.getName());
                claims.put("surname", user.getSurname());
                claims.put("patronymic", user.getPatronymic());
                claims.put("role", user.getRole().toString());
            }
        } catch (Exception e) {
            throw new CustomException("Error getting user");
        }

        checkDTO.setId(checkEntity.getId());
        checkDTO.setDate(checkEntity.getDate());
        checkDTO.setUsername(claims.get("username"));
        checkDTO.setName(claims.get("name"));
        checkDTO.setSurname(claims.get("surname"));
        checkDTO.setPatronymic(claims.get("patronymic"));
        checkDTO.setRole(claims.get("role"));
        return checkDTO;
    }
}
