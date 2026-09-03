package com.arazhafez.academe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAssignmentRequest {

    @NotBlank(message = "Assignment title is required")
    private String title;

    private String description;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @NotNull(message = "Maximum points are required")
    @Min(value = 1, message = "Maximum points must be at least 1")
    private Integer maxPoints;
}