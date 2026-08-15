package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.CourseResponse;
import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            Authentication authentication) {

        //Get the logged-in instructor's email from the JWT
        String instructorEmail = authentication.getName();

        CourseResponse createdCourse =
                courseService.createCourse(
                        request,
                        instructorEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCourse);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {

        List<CourseResponse> courses =
                courseService.getAllCourses();

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {

        CourseResponse course =
                courseService.getCourseById(id);

        return ResponseEntity.ok(course);
    }
}