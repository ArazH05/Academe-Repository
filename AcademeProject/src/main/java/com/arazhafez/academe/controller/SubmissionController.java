package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.CreateSubmissionRequest;
import com.arazhafez.academe.dto.SubmissionResponse;
import com.arazhafez.academe.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(
            SubmissionService submissionService) {

        this.submissionService = submissionService;
    }

    @PostMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> createSubmission(
            @PathVariable Long assignmentId,
            @Valid @RequestBody CreateSubmissionRequest request,
            Authentication authentication) {

        String studentEmail =
                authentication.getName();

        SubmissionResponse submission =
                submissionService.createSubmission(
                        assignmentId,
                        request,
                        studentEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(submission);
    }

    @GetMapping("/submissions/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionResponse>>
    getMySubmissions(Authentication authentication) {

        String studentEmail =
                authentication.getName();

        List<SubmissionResponse> submissions =
                submissionService.getMySubmissions(
                        studentEmail
                );

        return ResponseEntity.ok(submissions);
    }
}