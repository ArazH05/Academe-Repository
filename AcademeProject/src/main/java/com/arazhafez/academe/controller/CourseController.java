package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.entity.Course;
import com.arazhafez.academe.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            Authentication authentication) {

        //Email comes from the authenticated JWT
        String instructorEmail = authentication.getName();

        Course createdCourse =
                courseService.createCourse(
                        request,
                        instructorEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCourse);
    }
}