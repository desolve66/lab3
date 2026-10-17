package com.example.newsroom.dto;

import com.example.newsroom.model.IssueStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class EditionIssueDto {
    private Long id;
    private LocalDate issueDate; // Выставляется сервером
    private IssueStatus status;
    private List<Long> articleIds;
}