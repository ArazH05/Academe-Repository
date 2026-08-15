package com.arazhafez.academe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String semester;
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private LocalDateTime enrolledAt;
}