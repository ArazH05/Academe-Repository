package com.arazhafez.academe.repository;

import com.arazhafez.academe.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository
        extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourseId(Long courseId);
    void deleteAllByCourseId(Long courseId);
}