package com.arazhafez.academe.service;

import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.enums.Role;
import com.arazhafez.academe.repository.CourseRepository;
import com.arazhafez.academe.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public Course createCourse(
            CreateCourseRequest request,
            String instructorEmail) {

        //Find the logged-in instructor
        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Instructor not found")
                );

        //Extra safety check even though controller will also be protected
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
                request.getCourseCode().trim().toUpperCase()
        );

        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription());
        course.setSemester(request.getSemester().trim());
        course.setCredits(request.getCredits());

        course.setJoinCode(
                request.getJoinCode().trim().toUpperCase()
        );

        //Associate the course with the logged-in instructor
        course.setInstructor(instructor);

        return courseRepository.save(course);
    }
}