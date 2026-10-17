package com.example.newsroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "edition_issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditionIssue extends BaseEntity {

    @Column(nullable = false)
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.DRAFT;

    @ManyToMany
    @JoinTable(
            name = "issue_articles",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles = new ArrayList<>();
}