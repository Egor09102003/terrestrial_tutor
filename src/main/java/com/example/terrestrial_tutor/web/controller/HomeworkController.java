package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.SelectionDTO;
import com.example.terrestrial_tutor.dto.TutorListDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkFacade;
import com.example.terrestrial_tutor.dto.facade.TutorListFacade;
import com.example.terrestrial_tutor.entity.HomeworkEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SubjectEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.service.HomeworkService;
import com.example.terrestrial_tutor.service.SubjectService;
import com.example.terrestrial_tutor.service.TaskService;
import com.example.terrestrial_tutor.service.TutorService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * Контроллер для работы с дз
 */
@RequiredArgsConstructor
@Controller
@Api
public class HomeworkController {
    @NonNull
    HomeworkService homeworkService;
    @NonNull
    TaskService taskService;
    @NonNull
    SubjectService subjectService;
    @NonNull
    TutorListFacade tutorListFacade;
    @NonNull
    TutorService tutorService;
    @NonNull
    HomeworkFacade homeworkFacade;

    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);

    //todo контроллер для обработки завершенной домашки

    /**
     * Сохранение дз
     *
     * @param homeworkDTO дз
     * @return сохраненное дз
     */
    @PostMapping("/homework/save")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<HomeworkDTO> saveHomework(@RequestBody HomeworkDTO homeworkDTO) {
        HomeworkEntity updatedHomework = homeworkFacade.homeworkDTOToHomework(homeworkDTO);
        HomeworkEntity currentHomework = homeworkService.getHomeworkById(updatedHomework.getId());
        TutorEntity currentTutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentHomework != null) {
            Set<PupilEntity> updatedPupils = new HashSet<>();
            updatedPupils.addAll(currentHomework.getPupils());
            updatedPupils.addAll(updatedHomework.getPupils());
            List<Long> currentPupilIds = currentTutor.getPupils().stream().map(PupilEntity::getId).toList();
            for (PupilEntity pupil : currentHomework.getPupils()) {
                if ((updatedHomework.getPupils().isEmpty() ||!updatedHomework.getPupils().contains(pupil))
                        && currentPupilIds.contains(pupil.getId()))
                {
                    updatedPupils.remove(pupil);
                }
            }
            currentHomework.setName(updatedHomework.getName());
            currentHomework.setSoluteTime(updatedHomework.getSoluteTime());
            currentHomework.setTargetTime(updatedHomework.getTargetTime());
            currentHomework.setTaskCheckingTypes(updatedHomework.getTaskCheckingTypes());
            currentHomework.setDeadLine(updatedHomework.getDeadLine());
            currentHomework.setPupils(updatedPupils);
        } else {
            currentHomework = updatedHomework;
            currentHomework.getTutors().add(currentTutor);
        }
        currentHomework = homeworkService.saveHomework(currentHomework);
        return new ResponseEntity<>(homeworkFacade.homeworkToHomeworkDTO(currentHomework), HttpStatus.OK);
    }

    /**
     * Контроллер для отдачи случайной выборки заданий по заданным данным и формированием в дз
     *
     * @param selectionDTO - входные данные, см. "SelectionDTO"
     * @return лист выборки
     */
    @PostMapping("/homework/selection")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<List<TaskEntity>> getTasksSelection(@RequestBody SelectionDTO selectionDTO) {
        SubjectEntity currentSubject = subjectService.findSubjectByName(selectionDTO.getSubject());
        List<TaskEntity> result = taskService.getSelectionTask(selectionDTO.getChoices(), currentSubject);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Поиск дз по id
     *
     * @param id id дз
     * @return дз
     */
    @GetMapping("/homework/{id}")
    public ResponseEntity<HomeworkDTO> getHomeworkById(@PathVariable Long id) {
        HomeworkEntity homework = homeworkService.getHomeworkById(id);
        return new ResponseEntity<>(homeworkFacade.homeworkToHomeworkDTO(homework), HttpStatus.OK);
    }

    /**
     * Контроллер для отдачи списка выполненных дз ученика
     *
     * @param id - id ученика
     * @return список выполненных дз ученика
     */
    @GetMapping("/pupil/{id}/homework/completed")
    public ResponseEntity<?> getCompletedHomework(@PathVariable Long id) {
        try {
            PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Set<HomeworkEntity> homeworks = homeworkService.getCompletedHomework(pupil);
            List<HomeworkDTO> homeworkDTOs = homeworks.stream().map(homework -> homeworkFacade.homeworkToHomeworkDTO(homework)).toList();
            return new ResponseEntity<>(homeworkDTOs, HttpStatus.OK);
        } catch (ClassCastException e) {
            return new ResponseEntity<>("Invalid user.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
    }

    /**
     * Удаление дз по id
     *
     * @param id id дз
     * @return статус операции
     */
    @DeleteMapping("/homework/delete/{id}")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<Long> deleteHomeworkById(@PathVariable Long id) {
        return new ResponseEntity<>(homeworkService.deleteHomeworkById(id), HttpStatus.OK);
    }

    /**
     * Поиск всех дз
     *
     * @return все дз
     */
    @GetMapping("/homework/all")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<List<HomeworkDTO>> getHomeworks() {
        List<HomeworkEntity> allHomeworks = homeworkService.getAllHomeworks();
        List<HomeworkDTO> allHomeworksDto = new ArrayList<>();
        for(HomeworkEntity homework : allHomeworks) {
            if (!homework.getName().isEmpty()) {
                allHomeworksDto.add(homeworkFacade.homeworkToHomeworkDTO(homework));
            }
        }
        return new ResponseEntity<>(allHomeworksDto, HttpStatus.OK);
    }

    /**
     * Get homeworks by pupil and subject
     *
     * @param pupilId - pupil id
     * @param subject - pupil subject
     * @return - list of homework DTO
     */
    @GetMapping("/homeworks/{pupilId}/{subject}")
    public ResponseEntity<List<HomeworkDTO>> getHomeworksByPupilAndSubject(@PathVariable Long pupilId, @PathVariable String subject) {
        List<HomeworkEntity> homeworks = homeworkService.getHomeworksByPupilAndSubject(pupilId, subject);
        List<HomeworkDTO> homeworkDTOs = new ArrayList<>();
        for (HomeworkEntity homeworkEntity : homeworks) {
            homeworkDTOs.add(homeworkFacade.homeworkToHomeworkDTO(homeworkEntity));
        }
        return new ResponseEntity<>(homeworkDTOs, HttpStatus.OK);
    }

    /**
     * Get homeworks answers (only if homework solution exists)
     *
     * @param homeworkId - homework id
     * @return - answers map
     */
    @GetMapping("homework/{homeworkId}/answers/right")
    public ResponseEntity<HashMap<Long, String>> getHomeworkRightAnswers(@PathVariable Long homeworkId) {
        return new ResponseEntity<>(homeworkService.getHomeworkAnswers(homeworkId), HttpStatus.OK);
    }

    /**
     * Get homework by id
     *
     * @param id - homework id
     * @return homework DTO
     */
    @GetMapping("/homework/pupil/{id}")
    public ResponseEntity<HomeworkDTO> getHomework(@PathVariable Long id) {
        return new ResponseEntity<>(
            homeworkFacade.homeworkToHomeworkDTO(homeworkService.getHomeworkByIdForCurrentPupil(id)),
            HttpStatus.OK
        );
    }

    /**
     * Get all homework tutors
     * 
     * @param homeworkId homework id
     * @return list of TutorListDTO
     */
    @GetMapping("/homework/{homeworkId}/tutors")
    public ResponseEntity<List<TutorListDTO>> getHomeworkTutors(@PathVariable Long homeworkId) {
        List<TutorEntity> tutors = new ArrayList<>(homeworkService.getHomeworkById(homeworkId).getTutors());
        return new ResponseEntity<>(tutorListFacade.tutorListToDTO(tutors), HttpStatus.OK);
    }

    @PatchMapping("homework/{homeworkId}/set/tutors")
    public ResponseEntity<HomeworkDTO> setHomeworkTutors(@PathVariable Long homeworkId, @RequestBody List<Long> tutorIds) {
        HomeworkEntity homework = homeworkService.getHomeworkById(homeworkId);
        List<TutorEntity> tutors = tutorService.getTutorByIds(tutorIds);
        homework.setTutors(new HashSet<>(tutors));
        homeworkService.saveHomework(homework);
        return new ResponseEntity<>(homeworkFacade.homeworkToHomeworkDTO(homework), HttpStatus.OK);
    }
    
}
