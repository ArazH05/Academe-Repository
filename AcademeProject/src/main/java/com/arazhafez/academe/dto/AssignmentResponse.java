package com.arazhafez.academe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AssignmentResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxPoints;

    private Long courseId;
    private String courseCode;
    private String courseTitle;

    private LocalDateTime createdAt;
}