package com.example.terrestrial_tutor.specification.task;

import org.springframework.data.jpa.domain.Specification;

import com.example.terrestrial_tutor.entity.TaskEntity;

public class TaskSpecifications {
    public static Specification<TaskEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<TaskEntity> level1Contains(String level1) {
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("level1")), "%" + level1.toLowerCase() + "%");
    }

    public static Specification<TaskEntity> level2Contains(String level2) {
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("level2")), "%" + level2.toLowerCase() + "%");
    }

    public static Specification<TaskEntity> idContains(Long id) {
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.function("str", String.class, root.get("id")),
                     "%" + id.toString() + "%");
    }

    public static Specification<TaskEntity> subjectContains(Long id) {
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.function("str", String.class, root.get("subject")),
                     "%" + id.toString() + "%");
    }

    public static Specification<TaskEntity> emptySpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}
