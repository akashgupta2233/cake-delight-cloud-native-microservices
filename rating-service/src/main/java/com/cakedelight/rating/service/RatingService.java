package com.cakedelight.rating.service;

import com.cakedelight.rating.entity.Rating;

import java.util.List;

public interface RatingService {
    Rating createRating(Rating rating);
    List<Rating> getRatingsByCakeId(Long cakeId);
    double getAverageRating(Long cakeId);
}
