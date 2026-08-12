package com.cakedelight.rating.service.impl;

import com.cakedelight.rating.entity.Rating;
import com.cakedelight.rating.repository.RatingRepository;
import com.cakedelight.rating.service.RatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

/**
 * Service implementation for rating management.
 * Handles rating creation, retrieval, and average calculation.
 */
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Rating createRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getRatingsByCakeId(Long cakeId) {
        return ratingRepository.findByCakeId(cakeId);
    }

    /**
     * Calculates the average rating for a specific cake.
     *
     * @param cakeId the cake identifier
     * @return average rating value, or 0.0 if no ratings exist
     */
    @Override
    public double getAverageRating(Long cakeId) {
        List<Rating> list = ratingRepository.findByCakeId(cakeId);
        if (list.isEmpty()) return 0.0;
        OptionalDouble avg = list.stream().mapToInt(Rating::getRatingValue).average();
        return avg.isPresent() ? avg.getAsDouble() : 0.0;
    }
}
