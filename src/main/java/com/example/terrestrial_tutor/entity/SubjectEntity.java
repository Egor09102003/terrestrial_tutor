package com.example.terrestrial_tutor.entity;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @NonNull
    @Column(name = "name")
    String name;

    @NonNull
    @Column(name = "count_level")
    Integer countLevel;

    @OneToMany(mappedBy = "subject")
    Set<EnrollEntity> enrolls;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    List<TaskEntity> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    List<HomeworkEntity> homeworkList;

    public Set<TutorEntity> getTutors() {
        return new HashSet<TutorEntity>(this.enrolls
                .stream()
                .map(EnrollEntity::getTutor)
                .toList());
    }
}
