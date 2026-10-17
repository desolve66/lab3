package com.example.newsroom.controller;

import com.example.newsroom.dto.EditionIssueDto;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.EditionIssue;
import com.example.newsroom.service.EditionIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edition-issues")
@RequiredArgsConstructor
public class EditionIssueController {
    private final EditionIssueService issueService;

    private EditionIssueDto toDto(EditionIssue issue) {
        EditionIssueDto dto = new EditionIssueDto();
        dto.setId(issue.getId());
        dto.setIssueDate(issue.getIssueDate());
        dto.setStatus(issue.getStatus());
        dto.setArticleIds(issue.getArticles().stream().map(Article::getId).toList());
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EditionIssueDto> getAll() {
        return issueService.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EditionIssueDto getById(@PathVariable Long id) {
        return toDto(issueService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditionIssueDto create(@RequestBody EditionIssueDto dto) {
        EditionIssue issue = new EditionIssue();
        if (dto.getStatus() != null) {
            issue.setStatus(dto.getStatus());
        }
        return toDto(issueService.create(issue, dto.getArticleIds()));
    }

    // БИЗНЕС-ОПЕРАЦИЯ 3: Включение статьи в выпуск
    @PostMapping("/{issueId}/articles/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public EditionIssueDto addArticle(@PathVariable Long issueId, @PathVariable Long articleId) {
        return toDto(issueService.addArticleToIssue(issueId, articleId));
    }

    // БИЗНЕС-ОПЕРАЦИЯ 4: Публикация выпуска
    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    public EditionIssueDto publish(@PathVariable Long id) {
        return toDto(issueService.publish(id));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EditionIssueDto patch(@PathVariable Long id, @RequestBody EditionIssueDto dto) {
        EditionIssue patch = new EditionIssue();
        patch.setStatus(dto.getStatus());
        return toDto(issueService.patch(id, patch, dto.getArticleIds()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        issueService.delete(id);
    }
}