package com.example.terrestrial_tutor.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;


import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "tasks_checking", schema = "public")
public class TaskCheckingEntity  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homework")
    HomeworkEntity homework;

    @ManyToOne
    @JoinColumn(name = "task_id")
    TaskEntity task;

    @Column(name = "checking_type")
    TaskCheckingType checkingType = TaskCheckingType.INSTANCE;

    
}
