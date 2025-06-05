package org.example.akigatorapp.repositories;

import org.example.akigatorapp.models.GameSessionEntity;
import org.example.akigatorapp.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSessionEntity, Long> {
    Optional<GameSessionEntity> findByGameSessionId(Long sessionId);

    List<GameSessionEntity> findByUser(UserEntity user);
}
