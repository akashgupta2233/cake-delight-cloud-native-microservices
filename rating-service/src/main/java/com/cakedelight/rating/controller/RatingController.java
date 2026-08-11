package com.cakedelight.rating.controller;

import com.cakedelight.rating.entity.Rating;
import com.cakedelight.rating.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@Valid @RequestBody Rating rating) {
        Rating saved = ratingService.createRating(rating);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/cake/{cakeId}")
    public ResponseEntity<List<Rating>> getRatingsByCakeId(@PathVariable Long cakeId) {
        List<Rating> list = ratingService.getRatingsByCakeId(cakeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/cake/{cakeId}/average")
    public ResponseEntity<Map<String, Object>> getAverage(@PathVariable Long cakeId) {
        double avg = ratingService.getAverageRating(cakeId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("cakeId", cakeId);
        resp.put("averageRating", Math.round(avg * 100.0) / 100.0); // round to 2 decimals
        return ResponseEntity.ok(resp);
    }
}
