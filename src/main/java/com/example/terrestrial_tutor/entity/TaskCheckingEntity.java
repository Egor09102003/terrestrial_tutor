package com.example.terrestrial_tutor.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;


import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;

/**
 * Сущность ответа ученика на задание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tasks_checking", schema = "public")
public class TaskCheckingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homework")
    HomeworkEntity homework;

    @ManyToOne
    @JoinColumn(name = "task")
    TaskEntity task;

    @Column(name = "checking_type")
    TaskCheckingType checkingType = TaskCheckingType.INSTANCE;
}
