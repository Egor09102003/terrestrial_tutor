package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.service.PupilService;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
@AllArgsConstructor
public class PupilFacade {

    @NonNull
    PupilService pupilService;
    @NonNull
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
        pupilDTO.setLessonPrice(pupil.getLessonPprice());
        LinkedList<String> subjects = new LinkedList<>(
            (
                pupil.getSubjects()
                .stream()
                .map(SubjectEntity::getName)
                .toList()
            )
        );
        subjects.sort(String::compareTo);
        pupilDTO.setSubjects(subjects);
        pupilDTO.setTutors((pupil.getTutors()
                .stream()
                .map(TutorEntity::getId)
                .toList()));
        pupilDTO.setUsername(pupil.getUsername());
        pupilDTO.setName(pupil.getName());
        pupilDTO.setSurname(pupil.getSurname());
        pupilDTO.setPatronymic(pupil.getPatronymic());

        return pupilDTO;
    }
}
