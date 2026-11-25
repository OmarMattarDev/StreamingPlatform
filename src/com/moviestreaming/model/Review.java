
package com.moviestreaming.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Review {

    // Fields
    private Integer reviewId;
    private Integer userId;
    private Integer movieId;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;

    // Constructor for new reviews
    public Review(Integer userId, Integer movieId,
                  Integer rating, String comment) {

        setUserId(userId);
        setMovieId(movieId);
        setRating(rating);
        setComment(comment);

        this.reviewDate = LocalDateTime.now();
    }

    // Constructor for database mapping
    public Review(Integer reviewId, Integer userId,
                  Integer movieId, Integer rating,
                  String comment, LocalDateTime reviewDate) {

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("Invalid review ID.");
        }

        this.reviewId = reviewId;

        setUserId(userId);
        setMovieId(movieId);
        setRating(rating);
        setComment(comment);

        validateReviewDate(reviewDate);
        this.reviewDate = reviewDate;
    }

    // Getters
    public Integer getReviewId() {
        return reviewId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    // Setters
    public void setUserId(Integer userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        this.userId = userId;
    }

    public void setMovieId(Integer movieId) {

        if (movieId == null || movieId <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        this.movieId = movieId;
    }

    public void setRating(Integer rating) {

        if (rating == null || rating < 1 || rating > 10) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 10.");
        }

        this.rating = rating;
    }

    public void setComment(String comment) {

        if (comment != null
                && comment.length() > 65535) {

            throw new IllegalArgumentException(
                    "Review comment is too long.");
        }

        this.comment = comment == null
                ? null : comment.trim();
    }

    // Validation Methods
    private void validateReviewDate(LocalDateTime date) {

        if (date == null || date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Invalid review date.");
        }
    }

    // Business Methods
    public boolean isPositiveReview() {
        return rating >= 7;
    }

    public boolean isNegativeReview() {
        return rating <= 4;
    }

    public boolean hasComment() {
        return comment != null && !comment.isBlank();
    }

    public void updateReview(Integer rating, String comment) {

        setRating(rating);
        setComment(comment);
    }

    // Object Methods
    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", userId=" + userId +
                ", movieId=" + movieId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Review)) {
            return false;
        }

        Review review = (Review) obj;

        return reviewId != null
                && reviewId.equals(review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reviewId);
    }
}
