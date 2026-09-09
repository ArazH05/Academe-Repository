package com.arazhafez.academe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="submissions",uniqueConstraints = {@UniqueConstraint(columnNames = {"assignment_id","student_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=5000)
    private String content;

    @Column(length=1000)
    private String fileUrl;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private User student;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    public void setSubmittedAt(){
        submittedAt = LocalDateTime.now();
    }
}