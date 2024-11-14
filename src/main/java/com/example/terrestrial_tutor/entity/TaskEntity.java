package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.entity.enums.AnswerTypes;
import com.google.gson.Gson;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
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
    AnswerTypes answerType;

    @ElementCollection(fetch = FetchType.EAGER)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support")
    SupportEntity support;

    @Column(name = "analysis", columnDefinition="text")
    String analysis;

    @Column(name = "cost")
    Integer cost;

    @Column(name = "crdate")
    Long crdate;

    @OneToMany(mappedBy = "task")
    List<TaskCheckingEntity> taskCheckingTypes = new ArrayList<>();

    @OneToMany(mappedBy = "task")
    List<AnswerEntity> pupilAnswers = new ArrayList<>();

    public String getRightAnswer() {
        return new Gson().fromJson(this.answer, String[].class)[0];
    }
}
