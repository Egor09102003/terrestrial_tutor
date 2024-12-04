package com.example.terrestrial_tutor.entity;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс сущности предмета
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "subjects", schema = "public")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NonNull
    @Column(name = "name")
    String name;

    @NonNull
    @Column(name = "count_level")
    Integer countLevel;

    @OneToMany(mappedBy = "subject")
    Set<EnrollmentEntity> enrollments;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    List<TaskEntity> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    List<HomeworkEntity> homeworkList;

    

    public Set<TutorEntity> getTutors() {
        return new HashSet<TutorEntity>(this.enrollments
                .stream()
                .map(EnrollmentEntity::getTutor)
                .toList());
    }
}
