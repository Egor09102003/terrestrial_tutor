package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;


import javax.persistence.*;

/**
 * Класс сущности администратора
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "enrolls", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uniq_record", columnNames = {"tutor", "pupil", "subject"})
})
public class EnrollEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrolls_sequence")
    @SequenceGenerator(name = "enrolls_sequence", sequenceName = "enrolls_sequence", allocationSize = 10)
    @Column(columnDefinition = "serial")
    private Long id;

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
