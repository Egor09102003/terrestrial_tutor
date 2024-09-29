package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.service.PupilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
public class PupilFacade {

    @Autowired
    PupilService pupilService;
    @Autowired
    HomeworkFacade homeworkFacade;

    /**
     * Метод перевода сущности ученика в DTO
     *
     * @param pupil ученик
     * @return DTO
     */

    public PupilDTO pupilToPupilDTO(PupilEntity pupil) {
        PupilDTO pupilDTO = new PupilDTO();
        pupilDTO.setId(pupil.getId());
        pupilDTO.setBalance(pupil.getBalance());
        pupilDTO.setPrice(pupil.getPrice());
        pupilDTO.setSubjects((pupil.getSubjects()
                .stream()
                .map(SubjectEntity::getName)
                .toList()));
        LinkedList<String> subjects = new LinkedList<>(pupilDTO.getSubjects());
        subjects.sort(String::compareTo);
        pupilDTO.setSubjects(subjects);
        pupilDTO.setTutors((pupil.getTutors()
                .stream()
                .map(tutorEntity -> tutorEntity.getSurname() + " " + tutorEntity.getName() + " " + tutorEntity.getPatronymic())
                .toList()));
        pupilDTO.setUsername(pupil.getUsername());
        pupilDTO.setName(pupil.getName());
        pupilDTO.setSurname(pupil.getSurname());
        pupilDTO.setPatronymic(pupil.getPatronymic());

        return pupilDTO;
    }
}
