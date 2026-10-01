package com.arazhafez.academe.repository;

import com.arazhafez.academe.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

    Optional<Submission> findByAssignmentIdAndStudentId(
            Long assignmentId,
            Long studentId
    );

    boolean existsByAssignmentIdAndStudentId(
            Long assignmentId,
            Long studentId
    );

    List<Submission> findByAssignmentId(
            Long assignmentId
    );

    List<Submission> findByStudentId(
            Long studentId
    );

    void deleteAllByAssignmentId(Long assignmentId);
}