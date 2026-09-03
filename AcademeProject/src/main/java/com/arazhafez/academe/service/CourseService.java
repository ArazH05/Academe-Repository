package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.CourseResponse;
import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.dto.InstructorCourseResponse;
import com.arazhafez.academe.dto.UpdateCourseRequest;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.exception.BadRequestException;
import com.arazhafez.academe.exception.ForbiddenException;
import com.arazhafez.academe.exception.ResourceNotFoundException;
import com.arazhafez.academe.repository.CourseRepository;
import com.arazhafez.academe.repository.EnrollmentRepository;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository) {

        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public InstructorCourseResponse createCourse(
            CreateCourseRequest request,
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
                    "Only instructors can create courses"
            );
        }

        String courseCode = request.getCourseCode()
                .trim()
                .toUpperCase();

        String joinCode = request.getJoinCode()
                .trim()
                .toUpperCase();

        if (courseRepository.existsByCourseCode(courseCode)) {
            throw new BadRequestException(
                    "Course code already exists"
            );
        }

        if (courseRepository.existsByJoinCode(joinCode)) {
            throw new BadRequestException(
                    "Join code already exists"
            );
        }

        Course course = new Course();

        course.setCourseCode(courseCode);
        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription());
        course.setSemester(request.getSemester().trim());
        course.setCredits(request.getCredits());
        course.setJoinCode(joinCode);
        course.setInstructor(instructor);

        Course savedCourse =
                courseRepository.save(course);

        return toInstructorCourseResponse(savedCourse);
    }

    public List<CourseResponse> getAllCourses() {

        return courseRepository
                .findAll()
                .stream()
                .map(this::toCourseResponse)
                .toList();
    }

    public CourseResponse getCourseById(Long id) {

        Course course = courseRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        return toCourseResponse(course);
    }

    public List<InstructorCourseResponse> getMyCourses(
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
                    "Only instructors can view their courses"
            );
        }

        return courseRepository
                .findByInstructorId(instructor.getId())
                .stream()
                .map(this::toInstructorCourseResponse)
                .toList();
    }

    public InstructorCourseResponse updateCourse(
            Long courseId,
            UpdateCourseRequest request,
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
                    "Only instructors can update courses"
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

        String courseCode = request.getCourseCode()
                .trim()
                .toUpperCase();

        String joinCode = request.getJoinCode()
                .trim()
                .toUpperCase();

        if (!course.getCourseCode().equals(courseCode)
                && courseRepository.existsByCourseCode(courseCode)) {

            throw new BadRequestException(
                    "Course code already exists"
            );
        }

        if (!course.getJoinCode().equals(joinCode)
                && courseRepository.existsByJoinCode(joinCode)) {

            throw new BadRequestException(
                    "Join code already exists"
            );
        }

        course.setCourseCode(courseCode);
        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription());
        course.setSemester(request.getSemester().trim());
        course.setCredits(request.getCredits());
        course.setJoinCode(joinCode);

        Course updatedCourse =
                courseRepository.save(course);

        return toInstructorCourseResponse(updatedCourse);
    }

    @Transactional
    public void deleteCourse(
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
                    "Only instructors can delete courses"
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

        enrollmentRepository.deleteAll(
                enrollmentRepository.findByCourseId(courseId)
        );

        courseRepository.delete(course);
    }

    // Used for general course information
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

    // Used when the instructor needs the join code
    private InstructorCourseResponse toInstructorCourseResponse(
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