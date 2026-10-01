package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.CreateSubmissionRequest;
import com.arazhafez.academe.dto.GradeSubmissionRequest;
import com.arazhafez.academe.dto.SubmissionResponse;
import com.arazhafez.academe.dto.UpdateSubmissionRequest;
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

        // Submission must be before the deadline
        if (LocalDateTime.now()
                .isAfter(assignment.getDueDate())) {

            throw new BadRequestException(
                    "The assignment deadline has passed"
            );
        }

        // Only one submission per assignment
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

    public SubmissionResponse updateSubmission(
            Long submissionId,
            UpdateSubmissionRequest request,
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
                    "Only students can update submissions"
            );
        }

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission not found"
                        )
                );

        // Student can only update their own submission
        if (!submission.getStudent()
                .getId()
                .equals(student.getId())) {

            throw new ForbiddenException(
                    "You cannot update another student's submission"
            );
        }

        // Cannot update after the assignment deadline
        if (LocalDateTime.now()
                .isAfter(
                        submission
                                .getAssignment()
                                .getDueDate()
                )) {

            throw new BadRequestException(
                    "The assignment deadline has passed"
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

        submission.setContent(
                request.getContent()
        );

        submission.setFileUrl(
                request.getFileUrl()
        );

        Submission updatedSubmission =
                submissionRepository.save(submission);

        return toSubmissionResponse(updatedSubmission);
    }

    public List<SubmissionResponse> getAssignmentSubmissions(
            Long assignmentId,
            String instructorEmail) {

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instructor not found"
                        )
                );

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new ForbiddenException(
                    "Only instructors can view assignment submissions"
            );
        }

        Assignment assignment = assignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found"
                        )
                );

        // Instructor must own the course
        if (!assignment.getCourse()
                .getInstructor()
                .getId()
                .equals(instructor.getId())) {

            throw new ForbiddenException(
                    "You are not the instructor of this course"
            );
        }

        return submissionRepository
                .findByAssignmentId(assignmentId)
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    public SubmissionResponse gradeSubmission(
            Long submissionId,
            GradeSubmissionRequest request,
            String instructorEmail) {

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instructor not found"
                        )
                );

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new ForbiddenException(
                    "Only instructors can grade submissions"
            );
        }

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission not found"
                        )
                );

        // Instructor must own the course
        if (!submission.getAssignment()
                .getCourse()
                .getInstructor()
                .getId()
                .equals(instructor.getId())) {

            throw new ForbiddenException(
                    "You are not the instructor of this course"
            );
        }

        // Grade cannot exceed assignment maximum
        if (request.getGrade()
                > submission.getAssignment().getMaxPoints()) {

            throw new BadRequestException(
                    "Grade cannot exceed assignment maximum points"
            );
        }

        submission.setGrade(
                request.getGrade()
        );

        submission.setFeedback(
                request.getFeedback()
        );

        Submission gradedSubmission =
                submissionRepository.save(submission);

        return toSubmissionResponse(gradedSubmission);
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

                submission.getGrade(),
                submission.getFeedback(),

                submission.getSubmittedAt()
        );
    }
}