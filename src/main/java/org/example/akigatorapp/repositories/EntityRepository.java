package org.example.akigatorapp.repositories;

import org.example.akigatorapp.models.CategoryEntity;
import org.example.akigatorapp.models.EntityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityRepository extends JpaRepository<EntityEntity, Long> {
    List<EntityEntity> findByCategory(CategoryEntity category);
}
