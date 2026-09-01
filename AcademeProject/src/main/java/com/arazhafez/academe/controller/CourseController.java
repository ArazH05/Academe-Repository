package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.CourseResponse;
import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.dto.EnrollmentResponse;
import com.arazhafez.academe.dto.UpdateCourseRequest;
import com.arazhafez.academe.service.CourseService;
import com.arazhafez.academe.service.EnrollmentService;
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
    private final EnrollmentService enrollmentService;

    public CourseController(
            CourseService courseService,
            EnrollmentService enrollmentService) {

        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
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

    //Instructor views students enrolled in one of their courses
    @GetMapping("/{courseId}/students")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponse>> getCourseStudents(
            @PathVariable Long courseId,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        List<EnrollmentResponse> students =
                enrollmentService.getCourseStudents(
                        courseId,
                        instructorEmail
                );

        return ResponseEntity.ok(students);
    }

    //Instructor updates one of their own courses
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        CourseResponse updatedCourse =
                courseService.updateCourse(
                        courseId,
                        request,
                        instructorEmail
                );

        return ResponseEntity.ok(updatedCourse);
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