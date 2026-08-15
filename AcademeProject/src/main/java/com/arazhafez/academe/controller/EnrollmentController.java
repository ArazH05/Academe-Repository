package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.EnrollmentResponse;
import com.arazhafez.academe.dto.JoinCourseRequest;
import com.arazhafez.academe.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService) {

        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> joinCourse(
            @Valid @RequestBody JoinCourseRequest request,
            Authentication authentication) {

        //Get the logged-in student's email from the JWT
        String studentEmail = authentication.getName();

        EnrollmentResponse enrollment =
                enrollmentService.joinCourse(
                        request,
                        studentEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(enrollment);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            Authentication authentication) {

        //Identify the student from the JWT
        String studentEmail = authentication.getName();

        List<EnrollmentResponse> enrollments =
                enrollmentService.getMyEnrollments(studentEmail);

        return ResponseEntity.ok(enrollments);
    }
}