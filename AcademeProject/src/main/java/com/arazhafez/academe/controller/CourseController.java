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

    public CourseController(
            CourseService courseService) {

        this.courseService = courseService;
    }

    //Instructor creates a new course
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        CourseResponse createdCourse =
                courseService.createCourse(
                        request,
                        instructorEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCourse);
    }

    //Get all courses
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {

        List<CourseResponse> courses =
                courseService.getAllCourses();

        return ResponseEntity.ok(courses);
    }

    //Instructor gets only their own courses
    @GetMapping("/mine")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        List<CourseResponse> courses =
                courseService.getMyCourses(
                        instructorEmail
                );

        return ResponseEntity.ok(courses);
    }

    //Get one course by its ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {

        CourseResponse course =
                courseService.getCourseById(id);

        return ResponseEntity.ok(course);
    }
}