package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.CreateSubmissionRequest;
import com.arazhafez.academe.dto.SubmissionResponse;
import com.arazhafez.academe.entity.Assignment;
import com.arazhafez.academe.entity.Submission;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.exception.BadRequestException;
import com.arazhafez.academe.exception.ForbiddenException;
import com.arazhafez.academe.exception.ResourceNotFoundException;
import com.arazhafez.academe.repository.AssignmentRepository;
import com.arazhafez.academe.repository.EnrollmentRepository;
import com.arazhafez.academe.repository.SubmissionRepository;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository) {

        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public SubmissionResponse createSubmission(
            Long assignmentId,
            CreateSubmissionRequest request,
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException(
                    "Only students can submit assignments"
            );
        }

        Assignment assignment = assignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found"
                        )
                );

        Long courseId =
                assignment.getCourse().getId();

        // Student must be enrolled in the course
        if (!enrollmentRepository
                .existsByStudentIdAndCourseId(
                        student.getId(),
                        courseId)) {

            throw new ForbiddenException(
                    "You are not enrolled in this course"
            );
        }

        // Do not accept submissions after the deadline
        if (LocalDateTime.now()
                .isAfter(assignment.getDueDate())) {

            throw new BadRequestException(
                    "The assignment deadline has passed"
            );
        }

        // Only one submission per student per assignment
        if (submissionRepository
                .existsByAssignmentIdAndStudentId(
                        assignmentId,
                        student.getId())) {

            throw new BadRequestException(
                    "You have already submitted this assignment"
            );
        }

        boolean noContent =
                request.getContent() == null
                        || request.getContent().isBlank();

        boolean noFile =
                request.getFileUrl() == null
                        || request.getFileUrl().isBlank();

        if (noContent && noFile) {
            throw new BadRequestException(
                    "Submission must contain content or a file"
            );
        }

        Submission submission = new Submission();

        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(request.getContent());
        submission.setFileUrl(request.getFileUrl());

        Submission savedSubmission =
                submissionRepository.save(submission);

        return toSubmissionResponse(savedSubmission);
    }

    public List<SubmissionResponse> getMySubmissions(
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException(
                    "Only students can view their submissions"
            );
        }

        return submissionRepository
                .findByStudentId(student.getId())
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    private SubmissionResponse toSubmissionResponse(
            Submission submission) {

        return new SubmissionResponse(
                submission.getId(),

                submission.getAssignment().getId(),
                submission.getAssignment().getTitle(),

                submission.getStudent().getId(),
                submission.getStudent().getFirstName(),
                submission.getStudent().getLastName(),

                submission.getContent(),
                submission.getFileUrl(),

                submission.getSubmittedAt()
        );
    }
}