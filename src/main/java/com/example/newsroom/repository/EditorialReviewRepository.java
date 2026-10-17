package com.example.newsroom.repository;

import com.example.newsroom.model.EditorialReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorialReviewRepository extends JpaRepository<EditorialReview, Long> {
}