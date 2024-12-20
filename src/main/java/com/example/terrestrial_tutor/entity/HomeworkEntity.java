package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;


import java.time.LocalDate;
import java.util.*;

/**
 * Класс сущности домашнего задания
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homeworks", schema = "public")
public class HomeworkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

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

    @Column(name = "task_checking_types", columnDefinition="text")
    String taskCheckingTypes;

    @OneToMany(mappedBy = "homework", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<AttemptEntity> answerEntities;

    LocalDate deadLine;
}
