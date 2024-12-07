package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.HomeworkDTO;
import com.example.terrestrial_tutor.dto.SelectionDTO;
import com.example.terrestrial_tutor.dto.TutorDTO;
import com.example.terrestrial_tutor.dto.facade.HomeworkMapper;
import com.example.terrestrial_tutor.dto.facade.TutorMapper;
import com.example.terrestrial_tutor.entity.*;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.payload.request.HomeworkSaveRequest;
import com.example.terrestrial_tutor.service.*;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    TutorMapper tutorMapper;
    @NonNull
    TutorService tutorService;
    @NonNull
    HomeworkMapper homeworkMapper;
    @NonNull
    PupilService pupilService;

    static final Logger log =
            LoggerFactory.getLogger(TerrestrialTutorApplication.class);

    /**
     * Сохранение дз
     *
     * @param homeworkSaveRequest дз
     * @return сохраненное дз
     */
    @PostMapping("/homework/save")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<HomeworkDTO> saveHomework(@RequestBody HomeworkSaveRequest homeworkSaveRequest) {
        HomeworkEntity updatedHomework = getHomeworkFromRequest(homeworkSaveRequest);
        HomeworkEntity currentHomework = homeworkService.getHomeworkById(updatedHomework.getId());
        TutorEntity currentTutor = (TutorEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentHomework != null) {
            updatedHomework.setTaskCheckingTypes(currentHomework.getTaskCheckingTypes());
            Set<PupilEntity> updatedPupils = new HashSet<>(currentHomework.getPupils());
            updatedPupils.addAll(updatedHomework.getPupils());
            List<Long> currentPupilIds = currentTutor.getPupils().stream().map(PupilEntity::getId).toList();
            for (PupilEntity pupil : currentHomework.getPupils()) {
                if ((updatedHomework.getPupils().isEmpty() ||!updatedHomework.getPupils().contains(pupil))
                        && currentPupilIds.contains(pupil.getId()))
                {
                    updatedPupils.remove(pupil);
                }
            }
            updatedHomework.setPupils(updatedPupils);
        } else {
            updatedHomework.getTutors().add(currentTutor);
        }
        updatedHomework = homeworkService.saveHomework(updatedHomework, homeworkSaveRequest.getTaskChecking());
        return new ResponseEntity<>(homeworkMapper.homeworkToHomeworkDTO(updatedHomework, true), HttpStatus.OK);
    }

    private HomeworkEntity getHomeworkFromRequest(HomeworkSaveRequest homeworkSaveRequest) {
        HomeworkEntity homework = new HomeworkEntity();
        homework.setId(homeworkSaveRequest.getId());
        homework.setSubject(subjectService.findSubjectByName(homeworkSaveRequest.getSubject()));
        homework.setName(homeworkSaveRequest.getName());
        homework.setPupils(new HashSet<>(pupilService.findPupilsByIds(homeworkSaveRequest.getPupilIds())));
        homework.setTargetTime(homeworkSaveRequest.getTargetTime());
        return homework;
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(homeworkMapper.homeworkToHomeworkDTO(homework, user.getRole() != ERole.PUPIL), HttpStatus.OK);
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
                allHomeworksDto.add(homeworkMapper.homeworkToHomeworkDTO(homework, true));
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (HomeworkEntity homeworkEntity : homeworks) {
            homeworkDTOs.add(homeworkMapper.homeworkToHomeworkDTO(homeworkEntity, user.getRole() != ERole.PUPIL));
        }
        return new ResponseEntity<>(homeworkDTOs, HttpStatus.OK);
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
            homeworkMapper.homeworkToHomeworkDTO(homeworkService.getHomeworkByIdForCurrentPupil(id), false),
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
    public ResponseEntity<List<TutorDTO>> getHomeworkTutors(@PathVariable Long homeworkId) {
        List<TutorEntity> tutors = new ArrayList<>(homeworkService.getHomeworkById(homeworkId).getTutors());
        return new ResponseEntity<>(tutorMapper.tutorListToDTO(tutors), HttpStatus.OK);
    }

    @PatchMapping("homework/{homeworkId}/set/tutors")
    public ResponseEntity<HomeworkDTO> setHomeworkTutors(@PathVariable Long homeworkId, @RequestBody List<Long> tutorIds) {
        HomeworkEntity homework = homeworkService.getHomeworkById(homeworkId);
        List<TutorEntity> tutors = tutorService.getTutorByIds(tutorIds);
        homework.setTutors(new HashSet<>(tutors));
        homeworkService.saveHomework(homework, null);
        return new ResponseEntity<>(homeworkMapper.homeworkToHomeworkDTO(homework, true), HttpStatus.OK);
    }
    
}
