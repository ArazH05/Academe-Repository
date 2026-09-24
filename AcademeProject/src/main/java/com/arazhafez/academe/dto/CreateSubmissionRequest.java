package com.arazhafez.academe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSubmissionRequest {
    private String content;
    private String fileUrl;
}
