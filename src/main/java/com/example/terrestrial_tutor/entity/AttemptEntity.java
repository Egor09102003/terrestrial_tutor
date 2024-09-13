package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.google.gson.JsonSyntaxException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.google.gson.Gson;

/**
 * Сущность ответа ученика на задание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homework_solutions", schema = "public")
public class AttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "answers", columnDefinition="text")
    String answers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework")
    HomeworkEntity homework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pupil")
    PupilEntity pupil;

    @Column(name = "attempt_number")
    Integer attemptNumber = 0;

    @Column(name = "status")
    HomeworkStatus status = HomeworkStatus.IN_PROGRESS;

    @Column(name = "solution_date")
    Long solutionDate = new Date().toInstant().toEpochMilli();

    public HomeworkAnswersDTO getAnswers() {
        try {
            HomeworkAnswersDTO answers = new Gson().fromJson(this.answers, HomeworkAnswersDTO.class);
            return answers;
        } catch (Exception e) {
            return new HomeworkAnswersDTO();
        }
    }

    public String getJSONAnswers() {
        return this.answers;
    }

    public void setAnswers(HomeworkAnswersDTO answers) {
        this.answers = new Gson().toJson(answers);
    }
}
