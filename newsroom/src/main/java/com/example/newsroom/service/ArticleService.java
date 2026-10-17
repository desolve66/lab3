package com.example.newsroom.service;

import com.example.newsroom.exception.BusinessException;
import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.ArticleStatus;
import com.example.newsroom.model.EditionIssue;
import com.example.newsroom.model.IssueStatus;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditionIssueRepository;
import com.example.newsroom.repository.EditorialSectionRepository;
import com.example.newsroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final EditorialSectionRepository sectionRepository;
    private final EditionIssueRepository issueRepository;

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article with id " + id + " not found"));
    }

    @Transactional
    public Article create(Article article, Long sectionId, Long authorId) {
        if (sectionId != null) {
            article.setSection(sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new NotFoundException("Section not found: " + sectionId)));
        }
        if (authorId != null) {
            article.setAuthor(userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("User not found: " + authorId)));
        }
        return articleRepository.save(article);
    }

    @Transactional
    public Article patch(Long id, Article patch, Long sectionId, Long authorId) {
        Article existing = findById(id);
        if (patch.getTitle() != null) existing.setTitle(patch.getTitle());
        if (patch.getText() != null) existing.setText(patch.getText());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        if (sectionId != null) {
            existing.setSection(sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new NotFoundException("Section not found: " + sectionId)));
        }
        if (authorId != null) {
            existing.setAuthor(userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("User not found: " + authorId)));
        }
        return articleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Article article = findById(id);
        List<EditionIssue> issues = issueRepository.findAll();
        for (EditionIssue issue : issues) {
            if (issue.getArticles().remove(article)) {
                issueRepository.save(issue);
            }
        }
        articleRepository.delete(article);
    }

    @Transactional
    public Article submitForReview(Long id) {
        Article article = findById(id);

        if (article.getStatus() != ArticleStatus.DRAFT) {
            throw new BusinessException("На проверку можно отправить только черновик (DRAFT). Текущий статус: " + article.getStatus());
        }

        if (article.getSection() == null || !Boolean.TRUE.equals(article.getSection().getIsAvailableForPublication())) {
            throw new BusinessException("Нельзя отправить статью на проверку: рубрика закрыта для публикаций или не указана!");
        }

        if (article.getText() == null || article.getText().trim().length() < 10) {
            throw new BusinessException("Текст статьи слишком короткий (минимум 10 символов)");
        }

        article.setStatus(ArticleStatus.IN_REVIEW);
        return articleRepository.save(article);
    }

    @Transactional
    public Article retract(Long id) {
        Article article = findById(id);

        if (article.getStatus() == ArticleStatus.DRAFT) {
            throw new BusinessException("Черновик нельзя отозвать, его можно просто удалить");
        }

        List<EditionIssue> issues = issueRepository.findAll();
        for (EditionIssue issue : issues) {
            if (issue.getStatus() != IssueStatus.PUBLISHED && issue.getArticles().remove(article)) {
                issueRepository.save(issue);
            }
        }

        article.setStatus(ArticleStatus.REJECTED);
        return articleRepository.save(article);
    }
}