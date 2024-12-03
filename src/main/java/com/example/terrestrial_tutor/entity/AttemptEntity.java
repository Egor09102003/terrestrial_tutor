package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
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
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность ответа ученика на задание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homework_solutions", schema = "public")
public class AttemptEntity implements Serializable {
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
    @MapKeyColumn(name = "task_id")
    Map<Long, AnswerEntity> answers = new HashMap<>();

    

    public AttemptEntity(PupilEntity pupil, Integer attemptNumber, HomeworkEntity homework) {
        this.pupil = pupil;
        this.attemptNumber = attemptNumber;
        this.homework = homework;
    }
}
