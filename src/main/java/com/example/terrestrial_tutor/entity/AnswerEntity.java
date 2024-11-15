package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;


import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.TaskStatuses;

/**
 * Сущность ответа ученика на задание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "pupil_answers", schema = "public")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "answer")
    String answer;

    @ManyToOne
    @JoinColumn(name = "task")
    TaskEntity task;

    @ManyToOne
    @JoinColumn(name = "attempt")
    AttemptEntity attempt;

    @Column(name = "points")
    Integer points = 0;

    @Column(name = "status")
    TaskStatuses status = TaskStatuses.ON_CHECKING;

    public AnswerEntity (AttemptEntity attempt, String answer, TaskEntity task) {
        this.attempt = attempt;
        this.answer = answer;
        this.task = task;
    }
}
