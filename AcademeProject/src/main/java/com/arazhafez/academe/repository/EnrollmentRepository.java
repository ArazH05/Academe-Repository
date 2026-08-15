package com.arazhafez.academe.repository;

import com.arazhafez.academe.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    //Get all enrollments for one student
    List<Enrollment> findByStudentId(Long studentId);
}