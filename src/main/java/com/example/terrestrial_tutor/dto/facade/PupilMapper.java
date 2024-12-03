package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;

import lombok.NonNull;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Класс для перевода сущности ученика в DTO
 */

@Component
@RequiredArgsConstructor
public class PupilMapper {

    @NonNull
    private final AttemptMapper attemptMapper;

    public PupilDTO toDTO(PupilEntity pupil) {
        return getDto(pupil);
    }

    public PupilDTO toDTOWithAttempt(PupilEntity pupil) {
        PupilDTO pupilDTO = getDto(pupil);
        for (AttemptEntity attempt : pupil.getHomeworkAttempts()) {
            if (pupilDTO.getLastAttempt() == null ||
                    pupilDTO.getLastAttempt().getAttemptNumber() > pupilDTO.getLastAttempt().getAttemptNumber()) {
                pupilDTO.setLastAttempt(attemptMapper.attemptToAttemptDTO(attempt, true));
            }
        }
        return pupilDTO;
    }

    private static PupilDTO getDto(PupilEntity pupil) {
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
        pupilDTO.setLastAttempt(null);

        return pupilDTO;
    }
}
