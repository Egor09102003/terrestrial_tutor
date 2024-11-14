package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;

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
    List<AnswerEntity> answers = new ArrayList<>();

    public AttemptEntity(PupilEntity pupil, Integer attemptNumber, HomeworkEntity homework) {
        this.pupil = pupil;
        this.attemptNumber = attemptNumber;
        this.homework = homework;
    }
}
