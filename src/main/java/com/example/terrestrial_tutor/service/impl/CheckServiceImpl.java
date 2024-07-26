package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.CheckEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.entity.User;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.exceptions.CheckException;
import com.example.terrestrial_tutor.repository.CheckRepository;
import com.example.terrestrial_tutor.service.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    @Autowired
    CheckRepository checkRepository;

    public List<CheckEntity> getAllChecks() {
        return checkRepository.findAll();
    }

    public CheckEntity getCheck(Long id) {
        return checkRepository.findCheckEntityById(id);
    }

    public CheckEntity addCheck(User newUser) {
        CheckEntity newCheck = new CheckEntity();
        newCheck.setDate(new Date());
        if (newUser instanceof PupilEntity pupil) {
            newCheck.setCandidateId(pupil.getId());
            newCheck.setRole(ERole.PUPIL);
        } else if (newUser instanceof TutorEntity tutor) {
            newCheck.setCandidateId(tutor.getId());
            newCheck.setRole(ERole.TUTOR);
        }
        try {
            return checkRepository.save(newCheck);
        } catch (Exception ex) {
            throw new CheckException("Error add check");
        }

    }

    public CheckEntity deleteCheck(Long id) {
        CheckEntity check = getCheck(id);
        try {
            checkRepository.delete(check);
            return check;
        } catch (Exception ex) {
            throw new CheckException("Error delete check");
        }
    }

    public CheckEntity findCheckById(Long id) {
        return checkRepository.findCheckEntityById(id);
    }

}
