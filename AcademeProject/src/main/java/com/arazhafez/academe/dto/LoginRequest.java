package com.arazhafez.academe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    //email user logs in with:
    @Email
    @NotBlank
    private String email;

    //plain text password entered by user:
    @NotBlank
    private String password;

    //NOTE: this is not for registering, just for logging in, i.e
    //we dont need all the details; already saved in academe_db
}
