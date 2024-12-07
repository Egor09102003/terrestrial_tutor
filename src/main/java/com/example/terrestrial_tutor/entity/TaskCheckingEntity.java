package com.example.terrestrial_tutor.entity;


import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "homework")
    HomeworkEntity homework;

    @ManyToOne
    @JoinColumn(name = "task_id")
    TaskEntity task;

    @Column(name = "checking_type")
    TaskCheckingType checkingType = TaskCheckingType.INSTANCE;

    @Column(name = "order_index")
    Integer orderIndex;

}
