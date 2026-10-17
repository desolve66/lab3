package com.example.newsroom.service;

import com.example.newsroom.exception.BusinessException;
import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.ArticleStatus;
import com.example.newsroom.model.EditionIssue;
import com.example.newsroom.model.IssueStatus;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditionIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditionIssueService {
    private final EditionIssueRepository issueRepository;
    private final ArticleRepository articleRepository;

    public List<EditionIssue> findAll() {
        return issueRepository.findAll();
    }

    public EditionIssue findById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));
    }

    @Transactional
    public EditionIssue create(EditionIssue issue, List<Long> articleIds) {
        issue.setIssueDate(LocalDate.now());
        if (articleIds != null && !articleIds.isEmpty()) {
            List<Article> articles = articleRepository.findAllById(articleIds);
            for (Article a : articles) {
                if (a.getStatus() != ArticleStatus.APPROVED) {
                    throw new BusinessException("В выпуск можно включить только одобренную статью (APPROVED). Статья '" + a.getTitle() + "' имеет статус: " + a.getStatus());
                }
            }
            issue.setArticles(articles);
        }
        return issueRepository.save(issue);
    }

    @Transactional
    public EditionIssue patch(Long id, EditionIssue patch, List<Long> articleIds) {
        EditionIssue existing = findById(id);
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());

            if (patch.getStatus() == IssueStatus.PUBLISHED) {
                for (Article a : existing.getArticles()) {
                    a.setStatus(ArticleStatus.PUBLISHED);
                    articleRepository.save(a);
                }
            }
        }
        if (articleIds != null) {
            List<Article> articles = articleRepository.findAllById(articleIds);
            existing.setArticles(articles);
        }
        return issueRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        EditionIssue issue = findById(id);
        issue.getArticles().clear();
        issueRepository.delete(issue);
    }

    @Transactional
    public EditionIssue addArticleToIssue(Long issueId, Long articleId) {
        EditionIssue issue = findById(issueId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("Article not found: " + articleId));

        if (issue.getStatus() == IssueStatus.PUBLISHED || issue.getStatus() == IssueStatus.ARCHIVED) {
            throw new BusinessException("Нельзя добавлять статьи в уже опубликованный или архивный номер!");
        }

        if (article.getStatus() != ArticleStatus.APPROVED) {
            throw new BusinessException("В номер можно включить только проверенную и одобренную статью (APPROVED). Текущий статус статьи: " + article.getStatus());
        }

        if (issue.getArticles().contains(article)) {
            throw new BusinessException("Данная статья уже включена в этот выпуск!");
        }

        issue.getArticles().add(article);
        return issueRepository.save(issue);
    }

    @Transactional
    public EditionIssue publish(Long id) {
        EditionIssue issue = findById(id);

        if (issue.getStatus() == IssueStatus.PUBLISHED) {
            throw new BusinessException("Выпуск уже был опубликован ранее!");
        }

        if (issue.getArticles().isEmpty()) {
            throw new BusinessException("Нельзя опубликовать пустой выпуск! Добавьте хотя бы одну статью.");
        }

        for (Article article : issue.getArticles()) {
            article.setStatus(ArticleStatus.PUBLISHED);
            articleRepository.save(article);
        }

        issue.setStatus(IssueStatus.PUBLISHED);
        issue.setIssueDate(LocalDate.now());

        return issueRepository.save(issue);
    }
}