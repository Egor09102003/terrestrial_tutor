package com.example.terrestrial_tutor.dto.facade;

import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.dto.PupilDTO;
import com.example.terrestrial_tutor.dto.TaskDTO;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.enums.AnswerTypes;
import com.example.terrestrial_tutor.service.PupilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
        pupilDTO.setHomeworks((pupil.getHomeworkList()
                .stream()
                .map(homework -> {
                    HomeworkDTO clearedHomework = homeworkFacade.homeworkToHomeworkDTO(homework);
                    List<TaskDTO> tasks = new ArrayList<>();
                    clearedHomework.getTasks().forEach(taskDTO -> {
                        switch (AnswerTypes.valueOf(taskDTO.getAnswerType())) {
                            case VARIANTS:
                                Collections.shuffle(taskDTO.getAnswers());
                                tasks.add(taskDTO);
                                break;
                            default:
                                TaskDTO clearedTask = new TaskDTO(taskDTO.getId(), taskDTO.getName(), taskDTO.getChecking(),
                                        taskDTO.getAnswerType(), taskDTO.getTaskText(), null, taskDTO.getSubject(),
                                        taskDTO.getLevel1(), taskDTO.getLevel2(), taskDTO.getTable(), taskDTO.getAnalysis(),
                                        taskDTO.getCost());
                                clearedTask.setFiles(taskDTO.getFiles());
                                tasks.add(clearedTask);
                        }
                    });
                    clearedHomework.setTasks(new LinkedList<>(tasks));
                    int lastAttempt = 0;
                    for (AttemptEntity attemptEntity : homework.getAnswerEntities()) {
                        if (attemptEntity.getPupil().getId().equals(pupil.getId())) {
                            lastAttempt = attemptEntity.getAttemptNumber() > lastAttempt ? attemptEntity.getAttemptNumber() : lastAttempt;
                        }
                    }
                    clearedHomework.setLastAttempt(lastAttempt);
                    return clearedHomework;
                })
                .toList()));
        pupilDTO.setPrice(pupil.getPrice());
        pupilDTO.setSubjects((pupil.getSubjects()
                .stream()
                .map(SubjectEntity::getName)
                .toList()));
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
