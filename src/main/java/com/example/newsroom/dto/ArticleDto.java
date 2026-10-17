package com.example.newsroom.dto;

import com.example.newsroom.model.ArticleStatus;
import lombok.Data;

@Data
public class ArticleDto {
    private Long id;
    private Long sectionId;
    private Long authorId;
    private String title;
    private String text;
    private ArticleStatus status;
}