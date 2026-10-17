package com.example.newsroom.service;

import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.EditorialSection;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditorialSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorialSectionService {
    private final EditorialSectionRepository sectionRepository;
    private final ArticleRepository articleRepository;

    public List<EditorialSection> findAll() {
        return sectionRepository.findAll();
    }

    public EditorialSection findById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section with id " + id + " not found"));
    }

    @Transactional
    public EditorialSection create(EditorialSection section) {
        return sectionRepository.save(section);
    }

    @Transactional
    public EditorialSection patch(Long id, EditorialSection patch) {
        EditorialSection existing = findById(id);
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getIsAvailableForPublication() != null) {
            existing.setIsAvailableForPublication(patch.getIsAvailableForPublication());
        }
        return sectionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        EditorialSection section = findById(id);
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            if (section.equals(article.getSection())) {
                article.setSection(null);
            }
        }
        sectionRepository.delete(section);
    }
}