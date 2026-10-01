package com.arazhafez.academe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSubmissionRequest {
    private String content;
    private String fileUrl;
}
