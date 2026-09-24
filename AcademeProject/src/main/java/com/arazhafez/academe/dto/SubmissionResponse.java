package com.arazhafez.academe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
}
