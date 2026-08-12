package com.cakedelight.rating.repository;

import com.cakedelight.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for rating data access.
 * Provides methods for querying ratings by cake.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByCakeId(Long cakeId);
}
