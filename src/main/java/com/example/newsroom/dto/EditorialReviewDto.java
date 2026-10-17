package com.example.newsroom.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EditorialReviewDto {
    private Long id;
    private Long articleId;
    private String decision;
    private String comments;
    private LocalDateTime reviewTime;
}