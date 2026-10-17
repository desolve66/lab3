package com.example.newsroom.dto;

import lombok.Data;

@Data
public class EditorialSectionDto {
    private Long id;
    private String name;
    private Boolean isAvailableForPublication;
}