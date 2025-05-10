package org.example.akigatorapp.repositories;

import org.example.akigatorapp.models.AnswerEntity;
import org.example.akigatorapp.models.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    List<AnswerEntity> findByQuestion(QuestionEntity currentQuestion);
}
