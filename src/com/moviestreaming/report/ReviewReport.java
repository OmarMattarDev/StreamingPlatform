
package com.moviestreaming.report;

import com.moviestreaming.dao.ReviewDAO;
import com.moviestreaming.model.Review;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReviewReport {

    private final ReviewDAO reviewDAO;

    public ReviewReport(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.reviewDAO = new ReviewDAO(connection);
    }

    public int getTotalReviews() throws SQLException {
        return reviewDAO.getAllReviews().size();
    }

    public double getAverageRatingByMovie(Integer movieId)
            throws SQLException {

        if (movieId == null || movieId <= 0) {
            throw new IllegalArgumentException(
                    "Movie ID must be greater than zero."
            );
        }

        List<Review> reviews = reviewDAO.getAllReviews()
                .stream()
                .filter(review ->
                        review.getMovieId().equals(movieId)
                )
                .toList();

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public long getPositiveReviewsCount()
            throws SQLException {

        return reviewDAO.getAllReviews()
                .stream()
                .filter(Review::isPositiveReview)
                .count();
    }

    public long getNegativeReviewsCount()
            throws SQLException {

        return reviewDAO.getAllReviews()
                .stream()
                .filter(Review::isNegativeReview)
                .count();
    }

    public Map<Integer, Double> getAverageRatingsByMovie()
            throws SQLException {

        return reviewDAO.getAllReviews()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Review::getMovieId,
                                Collectors.averagingInt(
                                        Review::getRating
                                )
                        )
                );
    }

    public List<Review> getReviewsSortedByRating()
            throws SQLException {

        return reviewDAO.getAllReviews()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                Review::getRating
                        ).reversed()
                )
                .toList();
    }

    public List<Review> getReviewsByMovie(Integer movieId)
            throws SQLException {

        if (movieId == null || movieId <= 0) {
            throw new IllegalArgumentException(
                    "Movie ID must be greater than zero."
            );
        }

        return reviewDAO.getAllReviews()
                .stream()
                .filter(review ->
                        review.getMovieId().equals(movieId)
                )
                .toList();
    }
}
