package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.CourseResponse;
import com.arazhafez.academe.dto.CreateCourseRequest;
import com.arazhafez.academe.dto.EnrollmentResponse;
import com.arazhafez.academe.dto.InstructorCourseResponse;
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

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<InstructorCourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        InstructorCourseResponse createdCourse =
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

    @GetMapping("/mine")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<InstructorCourseResponse>> getMyCourses(
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        List<InstructorCourseResponse> courses =
                courseService.getMyCourses(
                        instructorEmail
                );

        return ResponseEntity.ok(courses);
    }

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

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<InstructorCourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        InstructorCourseResponse updatedCourse =
                courseService.updateCourse(
                        courseId,
                        request,
                        instructorEmail
                );

        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            Authentication authentication) {

        String instructorEmail =
                authentication.getName();

        courseService.deleteCourse(
                courseId,
                instructorEmail
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {

        CourseResponse course =
                courseService.getCourseById(id);

        return ResponseEntity.ok(course);
    }
}