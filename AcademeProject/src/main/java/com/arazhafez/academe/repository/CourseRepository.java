package com.arazhafez.academe.repository;

import com.arazhafez.academe.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    //Check if a course code already exists
    boolean existsByCourseCode(String courseCode);

    //Check if a join code already exists
    boolean existsByJoinCode(String joinCode);

    //Find a course using its join code
    Optional<Course> findByJoinCode(String joinCode);

    //Find all courses belonging to an instructor
    List<Course> findByInstructorId(Long instructorId);
}