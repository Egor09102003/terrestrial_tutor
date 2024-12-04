package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.Timestamp;
/**
 * Класс сущности администратора
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "enrollments", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uniq_record", columnNames = {"tutor", "pupil", "subject"})
})
public class EnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollments_sequence")
    @SequenceGenerator(name = "enrollments_sequence", sequenceName = "enrollments_sequence", allocationSize = 10)
    @Column(columnDefinition = "serial")
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "tutor")
    TutorEntity tutor;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "pupil")
    PupilEntity pupil;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "subject")
    SubjectEntity subject;

    
}
