package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.EnrollmentResponse;
import com.arazhafez.academe.dto.JoinCourseRequest;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.Enrollment;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.exception.BadRequestException;
import com.arazhafez.academe.exception.ForbiddenException;
import com.arazhafez.academe.exception.ResourceNotFoundException;
import com.arazhafez.academe.repository.CourseRepository;
import com.arazhafez.academe.repository.EnrollmentRepository;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public EnrollmentResponse joinCourse(
            JoinCourseRequest request,
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
                    "Only students can join courses"
            );
        }

        String joinCode = request.getJoinCode()
                .trim()
                .toUpperCase();

        Course course = courseRepository
                .findByJoinCode(joinCode)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid join code"
                        )
                );

        if (enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(),
                course.getId())) {

            throw new BadRequestException(
                    "You are already enrolled in this course"
            );
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return toEnrollmentResponse(savedEnrollment);
    }

    public List<EnrollmentResponse> getMyEnrollments(
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
                    "Only students can view their enrollments"
            );
        }

        return enrollmentRepository
                .findByStudentId(student.getId())
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    public List<EnrollmentResponse> getCourseStudents(
            Long courseId,
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
                    "Only instructors can view course students"
            );
        }

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        if (!course.getInstructor().getId()
                .equals(instructor.getId())) {

            throw new ForbiddenException(
                    "You are not the instructor of this course"
            );
        }

        return enrollmentRepository
                .findByCourseId(courseId)
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    public void leaveCourse(
            Long courseId,
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
                    "Only students can leave courses"
            );
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(
                        student.getId(),
                        courseId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "You are not enrolled in this course"
                        )
                );

        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentResponse toEnrollmentResponse(
            Enrollment enrollment) {

        return new EnrollmentResponse(
                enrollment.getId(),

                enrollment.getCourse().getId(),
                enrollment.getCourse().getCourseCode(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getSemester(),

                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName(),
                enrollment.getStudent().getLastName(),

                enrollment.getEnrolledAt()
        );
    }
}