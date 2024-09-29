package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.entity.TutorEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Класс для перевода сущностей репетиторов в DTO
 */

@Component
public class TutorListFacade {

    /**
     * Метод для перевода сущностей репетиторов в DTO
     *
     * @param tutors репетиторы
     * @return DTO из списка репетиторов
     */
    public List<TutorListDTO> tutorListToDTO(Collection<TutorEntity> tutors) {
        return tutors
                .stream()
                .map(tutorEntity -> this.tutorToTutorDTO(tutorEntity))
                .toList();
    }

    public TutorListDTO tutorToTutorDTO(TutorEntity tutor) {
        TutorListDTO tutorDTO = new TutorListDTO();
        tutorDTO.setId(tutor.getId());
        tutorDTO.setName(tutor.getName());
        tutorDTO.setSurname(tutor.getSurname());
        tutorDTO.setPatronymic(tutor.getPatronymic());
        tutorDTO.setUsername(tutor.getUsername());
        return tutorDTO;
    }
}
