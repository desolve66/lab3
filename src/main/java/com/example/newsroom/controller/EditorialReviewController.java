package com.example.newsroom.controller;

import com.example.newsroom.dto.EditorialReviewDto;
import com.example.newsroom.model.EditorialReview;
import com.example.newsroom.service.EditorialReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editorial-reviews")
@RequiredArgsConstructor
public class EditorialReviewController {
    private final EditorialReviewService reviewService;

    private EditorialReviewDto toDto(EditorialReview review) {
        EditorialReviewDto dto = new EditorialReviewDto();
        dto.setId(review.getId());
        dto.setArticleId(review.getArticle() != null ? review.getArticle().getId() : null);
        dto.setDecision(review.getDecision());
        dto.setComments(review.getComments());
        dto.setReviewTime(review.getReviewTime());
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EditorialReviewDto> getAll() {
        return reviewService.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EditorialReviewDto getById(@PathVariable Long id) {
        return toDto(reviewService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorialReviewDto create(@RequestBody EditorialReviewDto dto) {
        EditorialReview review = new EditorialReview();
        review.setDecision(dto.getDecision());
        review.setComments(dto.getComments());
        return toDto(reviewService.create(review, dto.getArticleId()));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EditorialReviewDto patch(@PathVariable Long id, @RequestBody EditorialReviewDto dto) {
        EditorialReview patch = new EditorialReview();
        patch.setDecision(dto.getDecision());
        patch.setComments(dto.getComments());
        return toDto(reviewService.patch(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}