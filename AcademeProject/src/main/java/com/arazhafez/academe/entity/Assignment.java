package com.arazhafez.academe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Assignment title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @NotNull(message = "Due date is required")
    @Column(nullable = false)
    private LocalDateTime dueDate;

    @NotNull(message = "Maximum points are required")
    @Column(nullable = false)
    private Integer maxPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt() {
        createdAt = LocalDateTime.now();
    }
}
