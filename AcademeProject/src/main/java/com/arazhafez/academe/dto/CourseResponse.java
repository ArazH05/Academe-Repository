package com.arazhafez.academe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String title;
    private String description;
    private String semester;
    private Integer credits;
    private String joinCode;

    private Long instructorId;
    private String instructorFirstName;
    private String instructorLastName;

    private LocalDateTime createdAt;
}
