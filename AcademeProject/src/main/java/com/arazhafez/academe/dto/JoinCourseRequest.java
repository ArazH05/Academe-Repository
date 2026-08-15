package com.arazhafez.academe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinCourseRequest {

    @NotBlank(message = "Join code is required")
    private String joinCode;
}