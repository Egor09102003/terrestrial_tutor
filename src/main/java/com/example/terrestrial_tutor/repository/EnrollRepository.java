package com.example.terrestrial_tutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terrestrial_tutor.entity.EnrollEntity;

public interface EnrollRepository extends JpaRepository<EnrollEntity, Long> {
}
