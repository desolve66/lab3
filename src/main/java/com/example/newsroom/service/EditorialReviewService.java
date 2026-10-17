package com.example.newsroom.service;

import com.example.newsroom.exception.BusinessException;
import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.ArticleStatus;
import com.example.newsroom.model.EditorialReview;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditorialReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EditorialReviewService {

    private final EditorialReviewRepository reviewRepository;
    private final ArticleRepository articleRepository;

    public List<EditorialReview> findAll() {
        return reviewRepository.findAll();
    }

    public EditorialReview findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Review with id " + id + " not found"));
    }

    @Transactional
    public EditorialReview create(EditorialReview review, Long articleId) {
        if (articleId == null) {
            throw new BusinessException("articleId is required");
        }

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() ->
                        new NotFoundException("Article not found: " + articleId));

        String decision = normalizeDecision(review.getDecision());
        validateDecision(decision);

        if (article.getStatus() != ArticleStatus.IN_REVIEW) {
            throw new BusinessException("Рецензировать можно только статью в статусе IN_REVIEW! Текущий статус: " + article.getStatus());
        }

        if ("REJECTED".equals(decision) && (review.getComments() == null || review.getComments().trim().isEmpty())) {
            throw new BusinessException("При отклонении статьи обязательно указать причину в комментариях!");
        }

        review.setArticle(article);
        review.setDecision(decision);
        review.setReviewTime(LocalDateTime.now());

        updateArticleStatus(article, decision);

        return reviewRepository.save(review);
    }

    @Transactional
    public EditorialReview patch(Long id, EditorialReview patch) {
        EditorialReview existing = findById(id);
        Article article = existing.getArticle();

        if (patch.getDecision() != null) {
            String decision = normalizeDecision(patch.getDecision());
            validateDecision(decision);

            existing.setDecision(decision);
            updateArticleStatus(article, decision);
        }

        if (patch.getComments() != null) {
            existing.setComments(patch.getComments());
        }

        existing.setReviewTime(LocalDateTime.now());

        return reviewRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        EditorialReview review = findById(id);
        reviewRepository.delete(review);
    }

    private String normalizeDecision(String decision) {
        return decision == null
                ? ""
                : decision.trim().toUpperCase(Locale.ROOT);
    }

    private void validateDecision(String decision) {
        if (!"APPROVED".equals(decision)
                && !"REJECTED".equals(decision)) {
            throw new BusinessException("Decision must be APPROVED or REJECTED");
        }
    }

    private void updateArticleStatus(Article article, String decision) {
        if ("APPROVED".equals(decision)) {
            article.setStatus(ArticleStatus.APPROVED);
        } else {
            article.setStatus(ArticleStatus.REJECTED);
        }

        articleRepository.save(article);
    }
}