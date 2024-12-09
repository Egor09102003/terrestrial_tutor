package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.dto.HomeworkAnswersDTO;
import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Сущность ответа ученика на задание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homework_solutions", schema = "public")
public class AttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework")
    HomeworkEntity homework;

    @Column(name = "attempt_points")
    Long attemptPoints = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pupil")
    PupilEntity pupil;

    @Column(name = "attempt_number")
    Integer attemptNumber = 1;

    @Column(name = "status")
    HomeworkStatus status = HomeworkStatus.IN_PROGRESS;

    @Column(name = "solution_date")
    Long solutionDate = new Date().toInstant().toEpochMilli();

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    @MapKeyColumn(name = "task_id")
    Map<Long, AnswerEntity> answers = new HashMap<>();

    @Column(name = "answers_legacy", columnDefinition="text")
    String answersLegacy;

    public HomeworkAnswersDTO getAnswersLegacy() {
        try {
            HomeworkAnswersDTO answers = new Gson().fromJson(this.answersLegacy, HomeworkAnswersDTO.class);
            return answers;
        } catch (Exception e) {
            return new HomeworkAnswersDTO();
        }
    }

    public AttemptEntity(PupilEntity pupil, Integer attemptNumber, HomeworkEntity homework) {
        this.pupil = pupil;
        this.attemptNumber = attemptNumber;
        this.homework = homework;
    }
}
