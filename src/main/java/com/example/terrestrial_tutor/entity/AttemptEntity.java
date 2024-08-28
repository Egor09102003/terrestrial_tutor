package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.HomeworkStatus;
import com.example.terrestrial_tutor.entity.enums.TaskCheckingType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Сущность ответа ученика на задание
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homework_solutions", schema = "public")
public class AttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "answers", columnDefinition="text")
    String answers;

    @ManyToOne()
    @JoinColumn(name = "homework")
    HomeworkEntity homework;

    @ManyToOne()
    @JoinColumn(name = "pupil")
    PupilEntity pupil;

    @Column(name = "attempt_number")
    Integer attemptNumber = 0;

    @Column(name = "status")
    HomeworkStatus status = HomeworkStatus.IN_PROGRESS;

    @Column(name = "solution_date")
    Long solutionDate = new Date().toInstant().toEpochMilli();

    public HashMap<Long,String> getAnswers() {
        Type type = new TypeToken<HashMap<Long, String>>(){}.getType();
        return new Gson().fromJson(this.answers, type);
    }

    public void setAnswers(HashMap<Long, String> answers) {
        this.answers = new Gson().toJson(answers);
    }
}
