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

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return toEnrollmentResponse(savedEnrollment);
    }

    public List<EnrollmentResponse> getMyEnrollments(
            String studentEmail) {

        //Find the logged-in student
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

    public List<EnrollmentResponse> getCourseStudents(
            Long courseId,
            String instructorEmail) {

        //Find the logged-in instructor
        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Instructor not found")
                );

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException(
                    "Only instructors can view course students"
            );
        }

        //Find the requested course
        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Course not found")
                );

        //Make sure this instructor owns the course
        if (!course.getInstructor().getId()
                .equals(instructor.getId())) {

            throw new IllegalArgumentException(
                    "You are not the instructor of this course"
            );
        }

        //Return all students enrolled in the course
        return enrollmentRepository
                .findByCourseId(courseId)
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    public void leaveCourse(
            Long courseId,
            String studentEmail) {

        //Find the logged-in student
        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found")
                );

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can leave courses"
            );
        }

        //Find this student's enrollment in the requested course
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(
                        student.getId(),
                        courseId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "You are not enrolled in this course"
                        )
                );

        //Delete only this student's enrollment
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