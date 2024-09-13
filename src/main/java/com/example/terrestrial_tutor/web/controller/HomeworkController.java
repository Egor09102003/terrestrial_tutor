package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.TerrestrialTutorApplication;
import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
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

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;





/**
 * Контроллер для работы с дз
 */
@RequiredArgsConstructor
@Controller
@Api
public class HomeworkController {
    @Autowired
    HomeworkService homeworkService;
    @Autowired
    TaskService taskService;
    @Autowired
    HomeworkFacade homeworkFacade;
    @Autowired
    SubjectService subjectService;
    @Autowired
    TutorListFacade tutorListFacade;
    @Autowired
    TutorService tutorService;

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
        HomeworkEntity newHomework = homeworkService.saveHomework(homeworkFacade.homeworkDTOToHomework(homeworkDTO));
        HomeworkDTO newHomeworkDTO = homeworkFacade.homeworkToHomeworkDTO(newHomework);
        return new ResponseEntity<>(newHomeworkDTO, HttpStatus.OK);
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
     * Добавление ответов на дз
     *
     * @param answers    ответы
     * @param homeworkId id дз
     * @return дз с добавленной попыткой
     */
    @PutMapping("/homework/save/{homeworkId}")
    @Secured("hasAnyRole({'TUTOR', 'ADMIN'})")
    public ResponseEntity<HomeworkAnswersDTO> getCheckingAnswers(@RequestBody Map<Long, String> answers,
                                                                 @PathVariable Long homeworkId
                                                                 ) {
        HomeworkAnswersDTO homeworkAnswersDTO = homeworkService.checkingAndSaveAnswers(answers, homeworkId);
        return new ResponseEntity<>(homeworkAnswersDTO, HttpStatus.OK);
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
    public ResponseEntity<Map<Long, Long>> getCompletedHomework(@PathVariable Long id) {
        return new ResponseEntity<>(homeworkService.getCompletedHomework(id), HttpStatus.OK);
    }

    /**
     * Get last not finished attempt or create new
     *
     * @param id - homework id
     * @return - current attempt
     */
    @GetMapping(value = {"/homework/{id}/init"})
    public ResponseEntity<HomeworkAnswersDTO> initHomework(@PathVariable Long id) {
        return new ResponseEntity<>(homeworkService.initHomework(id), HttpStatus.OK);
    }

    /**
     * Get attempt results
     *
     * @param id - homework id
     * @param attempt - attempt id, if skipped, will be returned last attempt
     * @return - attempt answers and statuses
     */
    @GetMapping(value = {"/homework/{homeworkId}/answers/{attempt}", "/homework/{homeworkId}/answers"})
    public ResponseEntity<HomeworkAnswersDTO> getPupilAttempts(@PathVariable Long homeworkId,
                                                                 @PathVariable Optional<Integer> attempt,
                                                                 @RequestParam Optional<Long> pupilId) {
        Long id;
        if (!pupilId.isPresent()) {
            try {
                PupilEntity pupil = (PupilEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                id = pupil.getId();
            } catch(Exception e) {
                return new ResponseEntity<>(new HomeworkAnswersDTO(), HttpStatus.NOT_FOUND);
            }
        } else {
            id = pupilId.get();
        }
        return new ResponseEntity<>(homeworkService.getPupilAnswers(homeworkId, id, attempt), HttpStatus.OK);
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
            allHomeworksDto.add(homeworkFacade.homeworkToHomeworkDTO(homework));
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
     * Set finish status for homework
     * 
     * @param homeworkId - homework id
     * @param answers - pupil answers
     * @return answers DTO
     */
    @PutMapping("homework/finish/{homeworkId}")
    public ResponseEntity<HomeworkAnswersDTO> finishHomework(@PathVariable Long homeworkId, @RequestBody Map<Long, String> answers) {
        try {
            return new ResponseEntity<>(homeworkService.checkAndFinish(answers, homeworkId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new HomeworkAnswersDTO(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
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

    @GetMapping("/homeworks/repair")
    public ResponseEntity<String> getRepairHomeworks() {
        try {
            homeworkService.repairAttemptNumber();
            return new ResponseEntity<>("All attempts repaired", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PatchMapping("homework/{homeworkId}/pupil/{pupilId}")
    public ResponseEntity<HomeworkAnswersDTO> patchPupilAttempt(
            @PathVariable Long homeworkId,
            @PathVariable Long pupilId,
            @RequestBody HomeworkAnswersDTO updatedAnswers) {
        try {
            return new ResponseEntity<>(
                    homeworkService.manuallyChecking(updatedAnswers, pupilId, homeworkId),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(new HomeworkAnswersDTO(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Get all homework tutors
     * 
     * @param homeworkId homework id
     * @return list of TutorListDTO
     */
    @GetMapping("/homework/{homeworkId}/tutors")
    public ResponseEntity<List<TutorListDTO>> getHomeworkTutors(@PathVariable Long homeworkId) {
        List<TutorEntity> tutors = new ArrayList<>();
        tutors.addAll(homeworkService.getHomeworkById(homeworkId).getTutors());
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
