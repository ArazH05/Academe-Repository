package com.arazhafez.academe.controller;

import com.arazhafez.academe.dto.UserResponse;
import com.arazhafez.academe.entity.User;
import com.arazhafez.academe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        //Spring gets this email from our authenticated JWT
        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-test")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> studentTest() {

        //Only STUDENT accounts can reach this
        return ResponseEntity.ok("Student access granted");
    }

    @GetMapping("/instructor-test")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<String> instructorTest() {

        //Only INSTRUCTOR accounts can reach this
        return ResponseEntity.ok("Instructor access granted");
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminTest() {

        //Only ADMIN accounts can reach this
        return ResponseEntity.ok("Admin access granted");
    }
}