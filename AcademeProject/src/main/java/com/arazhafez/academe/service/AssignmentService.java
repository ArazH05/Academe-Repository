package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.AssignmentResponse;
import com.arazhafez.academe.dto.CreateAssignmentRequest;
import com.arazhafez.academe.dto.UpdateAssignmentRequest;
import com.arazhafez.academe.entity.Assignment;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.exception.BadRequestException;
import com.arazhafez.academe.exception.ForbiddenException;
import com.arazhafez.academe.exception.ResourceNotFoundException;
import com.arazhafez.academe.repository.AssignmentRepository;
import com.arazhafez.academe.repository.CourseRepository;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {

        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public AssignmentResponse createAssignment(
            Long courseId,
            CreateAssignmentRequest request,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        verifyCourseOwnership(course, instructor);

        if (request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    "Due date must be in the future"
            );
        }

        Assignment assignment = new Assignment();

        assignment.setTitle(request.getTitle().trim());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxPoints(request.getMaxPoints());
        assignment.setCourse(course);

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        return toAssignmentResponse(savedAssignment);
    }

    public List<AssignmentResponse> getAssignmentsByCourse(
            Long courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    "Course not found"
            );
        }

        return assignmentRepository
                .findByCourseId(courseId)
                .stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    public AssignmentResponse getAssignmentById(
            Long assignmentId) {

        Assignment assignment = assignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found"
                        )
                );

        return toAssignmentResponse(assignment);
    }

    public AssignmentResponse updateAssignment(
            Long assignmentId,
            UpdateAssignmentRequest request,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);

        Assignment assignment = assignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found"
                        )
                );

        verifyCourseOwnership(
                assignment.getCourse(),
                instructor
        );

        if (request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    "Due date must be in the future"
            );
        }

        assignment.setTitle(
                request.getTitle().trim()
        );

        assignment.setDescription(
                request.getDescription()
        );

        assignment.setDueDate(
                request.getDueDate()
        );

        assignment.setMaxPoints(
                request.getMaxPoints()
        );

        Assignment updatedAssignment =
                assignmentRepository.save(assignment);

        return toAssignmentResponse(updatedAssignment);
    }

    public void deleteAssignment(
            Long assignmentId,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);

        Assignment assignment = assignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found"
                        )
                );

        verifyCourseOwnership(
                assignment.getCourse(),
                instructor
        );

        assignmentRepository.delete(assignment);
    }

    private User getInstructor(
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
                    "Only instructors can manage assignments"
            );
        }

        return instructor;
    }

    private void verifyCourseOwnership(
            Course course,
            User instructor) {

        if (!course.getInstructor().getId()
                .equals(instructor.getId())) {

            throw new ForbiddenException(
                    "You are not the instructor of this course"
            );
        }
    }

    private AssignmentResponse toAssignmentResponse(
            Assignment assignment) {

        return new AssignmentResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxPoints(),

                assignment.getCourse().getId(),
                assignment.getCourse().getCourseCode(),
                assignment.getCourse().getTitle(),

                assignment.getCreatedAt()
        );
    }
}