package com.arazhafez.academe.dto;

import com.arazhafez.academe.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    //JWT token that is returned after a successful login
    private String token;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
