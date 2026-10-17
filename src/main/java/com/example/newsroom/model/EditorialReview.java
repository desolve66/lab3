package com.example.newsroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "editorial_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditorialReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private String decision;

    private String comments;

    @Column(nullable = false)
    private LocalDateTime reviewTime;
}