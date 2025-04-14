package org.example.akigatorapp.repositories;

import org.example.akigatorapp.models.CategoryEntity;
import org.example.akigatorapp.models.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByCategory(CategoryEntity category);
}
