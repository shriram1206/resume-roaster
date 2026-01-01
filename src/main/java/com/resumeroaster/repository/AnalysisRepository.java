package com.resumeroaster.repository;

import com.resumeroaster.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Analysis entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    /**
     * Find all analyses for a specific user, ordered by creation date (newest first).
     * @param userId the user's ID
     * @return list of analyses
     */
    List<Analysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find all analyses by tier placement.
     * @param tierPlacement the tier placement (e.g., "SERVICE_COMPANY")
     * @return list of analyses
     */
    List<Analysis> findByTierPlacement(String tierPlacement);

    /**
     * Find all analyses with overall score greater than or equal to the given value.
     * @param score the minimum score
     * @return list of analyses
     */
    List<Analysis> findByOverallScoreGreaterThanEqual(Integer score);
}
