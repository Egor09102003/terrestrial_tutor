package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.TaskCheckingEntity;
import com.example.terrestrial_tutor.repository.TaskCheckingRepository;
import com.example.terrestrial_tutor.service.TaskCheckingService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskCheckingServiceImpl implements TaskCheckingService {

    @NonNull
    TaskCheckingRepository taskCheckingRepository;

    public LinkedList<TaskCheckingEntity> findTaskCheckingsByIds(List<Long> ids) {
        return (LinkedList<TaskCheckingEntity>) taskCheckingRepository.findAllById(ids);
    }

    
}
