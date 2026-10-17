package com.example.newsroom.controller;

import com.example.newsroom.dto.ArticleDto;
import com.example.newsroom.model.Article;
import com.example.newsroom.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    private ArticleDto toDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setText(article.getText());
        dto.setStatus(article.getStatus());
        dto.setSectionId(article.getSection() != null ? article.getSection().getId() : null);
        dto.setAuthorId(article.getAuthor() != null ? article.getAuthor().getId() : null);
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ArticleDto> getAll() {
        return articleService.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto getById(@PathVariable Long id) {
        return toDto(articleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto create(@RequestBody ArticleDto dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setText(dto.getText());
        if (dto.getStatus() != null) {
            article.setStatus(dto.getStatus());
        }
        return toDto(articleService.create(article, dto.getSectionId(), dto.getAuthorId()));
    }

    @PostMapping("/{id}/submit")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto submit(@PathVariable Long id) {
        return toDto(articleService.submitForReview(id));
    }

    @PostMapping("/{id}/retract")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto retract(@PathVariable Long id) {
        return toDto(articleService.retract(id));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto patch(@PathVariable Long id, @RequestBody ArticleDto dto) {
        Article patch = new Article();
        patch.setTitle(dto.getTitle());
        patch.setText(dto.getText());
        patch.setStatus(dto.getStatus());
        return toDto(articleService.patch(id, patch, dto.getSectionId(), dto.getAuthorId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }
}