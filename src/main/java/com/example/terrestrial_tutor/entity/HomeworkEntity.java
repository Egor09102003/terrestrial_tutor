package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс сущности домашнего задания
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homeworks", schema = "public")
@SQLDelete(sql = "update homeworks set deleted=true where id=?")
@Where(clause = "deleted = false")
public class HomeworkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    Long id;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Column(name = "name")
    String name;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "subject")
    SubjectEntity subject;

    @Column(name = "solute_time")
    Long soluteTime;

    Long targetTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "pupils")
    Set<PupilEntity> pupils;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutors")
    Set<TutorEntity> tutors = new HashSet<>();

    @OneToMany(mappedBy = "homework", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<AttemptEntity> answerEntities;

    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name = "task_id")
    @OrderBy("orderIndex ASC")
    Map<Long, TaskCheckingEntity> taskCheckingTypes = new LinkedHashMap<>();

    LocalDate deadLine;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public List<TaskEntity> getTasks() {
        return this.taskCheckingTypes.values().stream()
                .map(TaskCheckingEntity::getTask)
                .toList();
    }
}
