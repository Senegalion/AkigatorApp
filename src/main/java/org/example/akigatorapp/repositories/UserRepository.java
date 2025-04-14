package org.example.akigatorapp.repositories;

import org.example.akigatorapp.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUserId(long id);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByResetToken(String resetToken);

    Boolean existsByEmail(String email);
}
