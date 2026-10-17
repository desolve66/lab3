package com.example.newsroom.repository;

import com.example.newsroom.model.EditorialSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorialSectionRepository extends JpaRepository<EditorialSection, Long> {
}