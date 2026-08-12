package com.arazhafez.academe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "courses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "course_code"),
                @UniqueConstraint(columnNames = "join_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course code is required")
    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @NotBlank(message = "Course title is required")
    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Semester is required")
    @Column(nullable = false, length = 30)
    private String semester;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits cannot exceed 6")
    @Column(nullable = false)
    private Integer credits;

    @NotBlank(message = "Join code is required")
    @Column(name = "join_code", nullable = false, length = 20)
    private String joinCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt() {
        createdAt = LocalDateTime.now();
    }
}