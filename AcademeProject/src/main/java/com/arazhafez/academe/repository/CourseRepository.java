package com.arazhafez.academe.repository;

import com.arazhafez.academe.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    //Useful for checking duplicate course codes
    boolean existsByCourseCode(String courseCode);

    //Useful for checking duplicate join codes
    boolean existsByJoinCode(String joinCode);

    Optional<Course> findByJoinCode(String joinCode);
}
