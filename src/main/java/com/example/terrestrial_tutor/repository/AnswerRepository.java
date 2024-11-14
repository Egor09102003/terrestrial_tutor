package com.example.terrestrial_tutor.repository;

import com.example.terrestrial_tutor.entity.AnswerEntity;
import com.example.terrestrial_tutor.entity.AttemptEntity;
import com.example.terrestrial_tutor.entity.TaskEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    public AnswerEntity findFirstByAttemptAndTask(AttemptEntity attempt, TaskEntity task);
}
