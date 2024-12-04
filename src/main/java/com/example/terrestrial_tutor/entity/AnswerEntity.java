package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.entity.enums.TaskStatuses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.Timestamp;
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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "answer")
    String answer;

    @ManyToOne
    @JoinColumn(name = "task_id")
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
