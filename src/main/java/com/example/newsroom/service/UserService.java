package com.example.newsroom.service;

import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.EditionIssue;
import com.example.newsroom.model.User;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditionIssueRepository;
import com.example.newsroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final EditionIssueRepository issueRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User patch(Long id, User patch) {
        User existing = findById(id);
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getEmail() != null) existing.setEmail(patch.getEmail());
        if (patch.getRole() != null) existing.setRole(patch.getRole());
        return userRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);

        List<Article> userArticles = new ArrayList<>();
        for (Article a : articleRepository.findAll()) {
            if (user.equals(a.getAuthor())) {
                userArticles.add(a);
            }
        }

        for (EditionIssue issue : issueRepository.findAll()) {
            issue.getArticles().removeAll(userArticles);
            issueRepository.save(issue);
        }

        articleRepository.deleteAll(userArticles);

        userRepository.delete(user);
    }
}