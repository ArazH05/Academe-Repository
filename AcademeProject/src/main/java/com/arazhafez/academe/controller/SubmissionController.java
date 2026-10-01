package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.CreateSubmissionRequest;
import com.arazhafez.academe.dto.GradeSubmissionRequest;
import com.arazhafez.academe.dto.SubmissionResponse;
import com.arazhafez.academe.dto.UpdateSubmissionRequest;
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

    // Student creates a submission
    @PostMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> createSubmission(
            @PathVariable Long assignmentId,
            @Valid @RequestBody CreateSubmissionRequest request,
            Authentication authentication) {

        SubmissionResponse submission =
                submissionService.createSubmission(
                        assignmentId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(submission);
    }

    // Student views their own submissions
    @GetMapping("/submissions/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionResponse>>
    getMySubmissions(
            Authentication authentication) {

        List<SubmissionResponse> submissions =
                submissionService.getMySubmissions(
                        authentication.getName()
                );

        return ResponseEntity.ok(submissions);
    }

    // Student updates their own submission
    @PutMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> updateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody UpdateSubmissionRequest request,
            Authentication authentication) {

        SubmissionResponse submission =
                submissionService.updateSubmission(
                        submissionId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(submission);
    }

    // Instructor views all submissions for an assignment
    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<SubmissionResponse>>
    getAssignmentSubmissions(
            @PathVariable Long assignmentId,
            Authentication authentication) {

        List<SubmissionResponse> submissions =
                submissionService.getAssignmentSubmissions(
                        assignmentId,
                        authentication.getName()
                );

        return ResponseEntity.ok(submissions);
    }

    // Instructor grades a submission
    @PutMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            Authentication authentication) {

        SubmissionResponse submission =
                submissionService.gradeSubmission(
                        submissionId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(submission);
    }
}