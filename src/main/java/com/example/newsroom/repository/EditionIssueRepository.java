package com.example.newsroom.repository;

import com.example.newsroom.model.EditionIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditionIssueRepository extends JpaRepository<EditionIssue, Long> {
}