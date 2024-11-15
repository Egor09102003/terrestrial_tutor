package com.example.terrestrial_tutor.service;

import java.util.LinkedList;
import java.util.List;

import com.example.terrestrial_tutor.entity.TaskCheckingEntity;

public interface TaskCheckingService {

    LinkedList<TaskCheckingEntity> findTaskCheckingsByIds(List<Long> ids);
}
