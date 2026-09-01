package com.arazhafez.academe.repository;

import com.arazhafez.academe.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    //Check if a student is already enrolled in a course
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    //Get all enrollments for one student
    List<Enrollment> findByStudentId(Long studentId);

    //Get all enrollments for one course
    List<Enrollment> findByCourseId(Long courseId);

    //Find one student's enrollment in one specific course
    Optional<Enrollment> findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );
}