package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.EnrollmentResponse;
import com.arazhafez.academe.dto.JoinCourseRequest;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.Enrollment;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
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

        //Find the logged-in student
        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found")
                );

        //Only students are allowed to enroll
        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can join courses"
            );
        }

        String joinCode = request.getJoinCode()
                .trim()
                .toUpperCase();

        //Find the course using the join code
        Course course = courseRepository
                .findByJoinCode(joinCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid join code")
                );

        //Prevent duplicate enrollment
        if (enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(),
                course.getId())) {

            throw new IllegalArgumentException(
                    "You are already enrolled in this course"
            );
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        //Save the enrollment first
        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        //Return a clean DTO instead of the entity
        return toEnrollmentResponse(savedEnrollment);
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

    public List<EnrollmentResponse> getMyEnrollments(String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found")
                );

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can view their enrollments"
            );
        }

        return enrollmentRepository
                .findByStudentId(student.getId())
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }
}