package com.arazhafez.academe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GradeSubmissionRequest {
    //Check to see if a grade is entered and ensures it is a valid grade; i.e > 0
    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade cannot be negative")
    private Integer grade;
    private String feedback;
}