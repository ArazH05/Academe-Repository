package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.CourseResponse;
import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.dto.InstructorCourseResponse;
import com.arazhafez.academe.dto.UpdateCourseRequest;
import com.arazhafez.academe.entity.Assignment;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.exception.BadRequestException;
import com.arazhafez.academe.exception.ForbiddenException;
import com.arazhafez.academe.exception.ResourceNotFoundException;
import com.arazhafez.academe.repository.AssignmentRepository;
import com.arazhafez.academe.repository.CourseRepository;
import com.arazhafez.academe.repository.EnrollmentRepository;
import com.arazhafez.academe.repository.SubmissionRepository;
import com.arazhafez.academe.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository,
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository) {

        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    // Instructor creates a course
    public InstructorCourseResponse createCourse(
            CreateCourseRequest request,
            String instructorEmail) {

        User instructor =
                getInstructor(instructorEmail);

        if (courseRepository.existsByCourseCode(
                request.getCourseCode())) {

            throw new BadRequestException(
                    "Course code already exists"
            );
        }

        if (courseRepository.existsByJoinCode(
                request.getJoinCode())) {

            throw new BadRequestException(
                    "Join code already exists"
            );
        }

        Course course = new Course();

        course.setCourseCode(
                request.getCourseCode()
        );

        course.setTitle(
                request.getTitle()
        );

        course.setDescription(
                request.getDescription()
        );

        course.setSemester(
                request.getSemester()
        );

        course.setCredits(
                request.getCredits()
        );

        course.setJoinCode(
                request.getJoinCode()
        );

        course.setInstructor(
                instructor
        );

        Course savedCourse =
                courseRepository.save(course);

        return toInstructorCourseResponse(
                savedCourse
        );
    }

    // Authenticated users can view courses
    // Join code is intentionally hidden
    public List<CourseResponse> getAllCourses() {

        return courseRepository
                .findAll()
                .stream()
                .map(this::toCourseResponse)
                .toList();
    }

    // Get one course
    // Join code is intentionally hidden
    public CourseResponse getCourseById(
            Long courseId) {

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        return toCourseResponse(course);
    }

    // Instructor views their own courses
    // Join code is included
    public List<InstructorCourseResponse> getMyCourses(
            String instructorEmail) {

        User instructor =
                getInstructor(instructorEmail);

        return courseRepository
                .findByInstructorId(
                        instructor.getId()
                )
                .stream()
                .map(this::toInstructorCourseResponse)
                .toList();
    }

    // Instructor updates their own course
    public InstructorCourseResponse updateCourse(
            Long courseId,
            UpdateCourseRequest request,
            String instructorEmail) {

        User instructor =
                getInstructor(instructorEmail);

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        verifyCourseOwnership(
                course,
                instructor
        );

        course.setTitle(
                request.getTitle()
        );

        course.setDescription(
                request.getDescription()
        );

        course.setSemester(
                request.getSemester()
        );

        course.setCredits(
                request.getCredits()
        );

        Course updatedCourse =
                courseRepository.save(course);

        return toInstructorCourseResponse(
                updatedCourse
        );
    }

    // Instructor deletes their own course
    @Transactional
    public void deleteCourse(
            Long courseId,
            String instructorEmail) {

        User instructor =
                getInstructor(instructorEmail);

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        verifyCourseOwnership(
                course,
                instructor
        );

        // Get all assignments in the course
        List<Assignment> assignments =
                assignmentRepository
                        .findByCourseId(courseId);

        // Delete submissions first
        for (Assignment assignment : assignments) {

            submissionRepository
                    .deleteAllByAssignmentId(
                            assignment.getId()
                    );
        }

        // Delete assignments
        assignmentRepository
                .deleteAllByCourseId(courseId);

        // Delete enrollments
        enrollmentRepository.deleteAll(
                enrollmentRepository
                        .findByCourseId(courseId)
        );

        // Finally delete the course
        courseRepository.delete(course);
    }

    // Finds and validates instructor
    private User getInstructor(
            String instructorEmail) {

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instructor not found"
                        )
                );

        if (instructor.getRole()
                != Role.INSTRUCTOR) {

            throw new ForbiddenException(
                    "Only instructors can perform this action"
            );
        }

        return instructor;
    }

    // Makes sure instructor owns the course
    private void verifyCourseOwnership(
            Course course,
            User instructor) {

        if (!course.getInstructor()
                .getId()
                .equals(instructor.getId())) {

            throw new ForbiddenException(
                    "You are not the instructor of this course"
            );
        }
    }

    // Public/student course response
    // Does not expose join code
    private CourseResponse toCourseResponse(
            Course course) {

        return new CourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getDescription(),
                course.getSemester(),
                course.getCredits(),

                course.getInstructor().getId(),
                course.getInstructor().getFirstName(),
                course.getInstructor().getLastName(),

                course.getCreatedAt()
        );
    }

    // Instructor response
    // Includes join code
    private InstructorCourseResponse
    toInstructorCourseResponse(
            Course course) {

        return new InstructorCourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getDescription(),
                course.getSemester(),
                course.getCredits(),
                course.getJoinCode(),

                course.getInstructor().getId(),
                course.getInstructor().getFirstName(),
                course.getInstructor().getLastName(),

                course.getCreatedAt()
        );
    }
}