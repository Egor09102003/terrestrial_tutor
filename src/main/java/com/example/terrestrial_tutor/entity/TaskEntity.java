package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

/**
 * Класс сущности задания
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tasks", schema = "public")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @NonNull
    @Column(name = "name")
    String name;

    @NonNull
    @Column(name = "checking")
    Integer checking;

    @NonNull
    @Column(name = "answer_type")
    String answerType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "task_files", joinColumns = @JoinColumn(name = "entity_id"))
    @Column(name = "files")
    Set<String> files;

    @Column(name = "task_text", columnDefinition="text")
    String taskText;

    @Column(name = "answers", columnDefinition="text")
    String answer;

    @ManyToOne()
    @JoinColumn(name = "subject")
    SubjectEntity subject;

    @NonNull
    @Column(name = "level1")
    String level1;

    @Column(name = "level2")
    String level2;

    @NonNull
    @Column(name = "tables")
    String table;

    @ManyToOne()
    @JoinColumn(name = "support")
    SupportEntity support;
}
