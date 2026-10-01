package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.AssignmentResponse;
import com.arazhafez.academe.dto.CreateAssignmentRequest;
import com.arazhafez.academe.dto.UpdateAssignmentRequest;
import com.arazhafez.academe.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(
            AssignmentService assignmentService) {

        this.assignmentService = assignmentService;
    }

    // Instructor creates an assignment
    @PostMapping("/courses/{courseId}/assignments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<AssignmentResponse> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAssignmentRequest request,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        AssignmentResponse assignment =
                assignmentService.createAssignment(
                        courseId,
                        request,
                        instructorEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assignment);
    }

    // Student or instructor views assignments for a course
    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentResponse>>
    getAssignmentsByCourse(
            @PathVariable Long courseId,
            Authentication authentication) {

        String userEmail =
                authentication.getName();

        List<AssignmentResponse> assignments =
                assignmentService.getAssignmentsByCourse(
                        courseId,
                        userEmail
                );

        return ResponseEntity.ok(assignments);
    }

    // Student or instructor views one assignment
    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(
            @PathVariable Long assignmentId,
            Authentication authentication) {

        String userEmail =
                authentication.getName();

        AssignmentResponse assignment =
                assignmentService.getAssignmentById(
                        assignmentId,
                        userEmail
                );

        return ResponseEntity.ok(assignment);
    }

    // Instructor updates an assignment
    @PutMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody UpdateAssignmentRequest request,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        AssignmentResponse assignment =
                assignmentService.updateAssignment(
                        assignmentId,
                        request,
                        instructorEmail
                );

        return ResponseEntity.ok(assignment);
    }

    // Instructor deletes an assignment
    @DeleteMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long assignmentId,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        assignmentService.deleteAssignment(
                assignmentId,
                instructorEmail
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}