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
@Table(name = "enrolls", schema = "public")
public class EnrollEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
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
