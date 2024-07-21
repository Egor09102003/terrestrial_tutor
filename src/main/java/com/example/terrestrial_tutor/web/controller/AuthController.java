package com.example.terrestrial_tutor.web.controller;

import com.example.terrestrial_tutor.annotations.Api;
import com.example.terrestrial_tutor.entity.AdminEntity;
import com.example.terrestrial_tutor.entity.PupilEntity;
import com.example.terrestrial_tutor.entity.SupportEntity;
import com.example.terrestrial_tutor.entity.TutorEntity;
import com.example.terrestrial_tutor.entity.enums.ERole;
import com.example.terrestrial_tutor.exceptions.NotAdminException;
import com.example.terrestrial_tutor.payload.request.LoginRequest;
import com.example.terrestrial_tutor.payload.request.RegistrationRequest;
import com.example.terrestrial_tutor.payload.response.JWTTokenSuccessResponse;
import com.example.terrestrial_tutor.payload.response.RegistrationSuccess;
import com.example.terrestrial_tutor.security.JWTTokenProvider;
import com.example.terrestrial_tutor.security.SecurityConstants;
//import com.example.terrestrial_tutor.service.CheckService;
import com.example.terrestrial_tutor.service.*;
import com.example.terrestrial_tutor.validators.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Контроллер для авторизации
 */
@CrossOrigin
@Api
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    JWTTokenProvider jwtTokenProvider;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;
    @Autowired
    PupilService pupilService;
    @Autowired
    TutorService tutorService;
    @Autowired
    CheckService checkService;
    @Autowired
    AdminService adminService;
    @Autowired
    SupportService supportService;

    @PostMapping("/auth/login")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        if (authentication.getPrincipal() instanceof AdminEntity) {
            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt, ERole.ADMIN));
        } else if (authentication.getPrincipal() instanceof PupilEntity) {
            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt, ERole.PUPIL));
        } else if (authentication.getPrincipal() instanceof SupportEntity) {
            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt, ERole.SUPPORT));
        } else {
            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt, ERole.TUTOR));
        }
    }

    @PostMapping("/auth/registration")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindResult);
        if (!ObjectUtils.isEmpty(errors)) {
            System.out.println(errors);
            return errors;
        }
        UserDetails newUser = null;
        if (registrationRequest.getRole() == ERole.PUPIL) {
            newUser = pupilService.addNewPupil(registrationRequest);
        } else if (registrationRequest.getRole() == ERole.TUTOR) {
            newUser = tutorService.addNewTutor(registrationRequest);
        } else {
            throw new NotAdminException();
        }
        checkService.addCheck(newUser);

        return new ResponseEntity<>(new RegistrationSuccess("User registration success"), HttpStatus.OK);
    }

    @PostMapping("/auth/registration/admin/{secret}")
    public ResponseEntity<Object> registerAdmin(@Valid @RequestBody RegistrationRequest registrationRequest
            , BindingResult bindResult, @PathVariable String secret) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindResult);
        if (!ObjectUtils.isEmpty(errors)) {
            System.out.println(errors);
            return errors;
        }
        if (registrationRequest.getRole() != ERole.ADMIN || !secret.equals("tErRrEsTrIaLtUtOr")) {
            throw new NotAdminException();
        }
        adminService.addNewAdmin(registrationRequest);

        return new ResponseEntity<>(new RegistrationSuccess("Admin registration success"), HttpStatus.OK);
    }

    @PostMapping("/auth/registration/support/{secret}")
    public ResponseEntity<Object> registerSupport(@Valid @RequestBody RegistrationRequest registrationRequest
            , BindingResult bindResult, @PathVariable String secret) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindResult);
        if (!ObjectUtils.isEmpty(errors)) {
            System.out.println(errors);
            return errors;
        }
        if (registrationRequest.getRole() != ERole.SUPPORT || !secret.equals("tErRrEsTrIaLtUtOr")) {
            throw new NotAdminException();
        }
        supportService.addNewSupport(registrationRequest);

        return new ResponseEntity<>(new RegistrationSuccess("Support registration success"), HttpStatus.OK);
    }

    @GetMapping("/auth/user/id")
    public ResponseEntity<Long> getCurrentUserId() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId;

        if (user instanceof PupilEntity pupil) {
            userId = pupil.getId();
        } else if (user instanceof TutorEntity tutor){
            userId = tutor.getId();
        } else if (user instanceof SupportEntity support){
            userId = support.getId();
        } else {
            AdminEntity admin = (AdminEntity) user;
            userId = admin.getId();
        }

        return new ResponseEntity<Long>(userId, HttpStatus.OK);
    }

}
