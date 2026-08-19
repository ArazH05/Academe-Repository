package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.CourseResponse;
import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.repository.CourseRepository;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository) {

        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public CourseResponse createCourse(
            CreateCourseRequest request,
            String instructorEmail) {

        //Find the logged-in instructor
        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Instructor not found")
                );

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException(
                    "Only instructors can create courses"
            );
        }

        if (courseRepository.existsByCourseCode(
                request.getCourseCode())) {

            throw new IllegalArgumentException(
                    "Course code already exists"
            );
        }

        if (courseRepository.existsByJoinCode(
                request.getJoinCode())) {

            throw new IllegalArgumentException(
                    "Join code already exists"
            );
        }

        Course course = new Course();

        course.setCourseCode(
                request.getCourseCode()
                        .trim()
                        .toUpperCase()
        );

        course.setTitle(
                request.getTitle().trim()
        );

        course.setDescription(
                request.getDescription()
        );

        course.setSemester(
                request.getSemester().trim()
        );

        course.setCredits(
                request.getCredits()
        );

        course.setJoinCode(
                request.getJoinCode()
                        .trim()
                        .toUpperCase()
        );

        //Attach course to the logged-in instructor
        course.setInstructor(instructor);

        Course savedCourse =
                courseRepository.save(course);

        return toCourseResponse(savedCourse);
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
                        new IllegalArgumentException(
                                "Course not found"
                        )
                );

        return toCourseResponse(course);
    }

    public List<CourseResponse> getMyCourses(
            String instructorEmail) {

        //Find the logged-in instructor
        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Instructor not found"
                        )
                );

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException(
                    "Only instructors can view their courses"
            );
        }

        return courseRepository
                .findByInstructorId(instructor.getId())
                .stream()
                .map(this::toCourseResponse)
                .toList();
    }

    //Convert Course entity into the DTO returned by the API
    private CourseResponse toCourseResponse(
            Course course) {

        return new CourseResponse(
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