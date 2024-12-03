package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.TutorDTO;
import com.example.terrestrial_tutor.entity.TutorEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Класс для перевода сущностей репетиторов в DTO
 */

@Component
public class TutorMapper {

    /**
     * Метод для перевода сущностей репетиторов в DTO
     *
     * @param tutors репетиторы
     * @return DTO из списка репетиторов
     */
    public List<TutorDTO> tutorListToDTO(Collection<TutorEntity> tutors) {
        return tutors
                .stream()
                .map(this::tutorToTutorDTO)
                .toList();
    }

    public TutorDTO tutorToTutorDTO(TutorEntity tutor) {
        TutorDTO tutorDTO = new TutorDTO();
        tutorDTO.setId(tutor.getId());
        tutorDTO.setName(tutor.getName());
        tutorDTO.setSurname(tutor.getSurname());
        tutorDTO.setPatronymic(tutor.getPatronymic());
        tutorDTO.setUsername(tutor.getUsername());
        return tutorDTO;
    }
}
