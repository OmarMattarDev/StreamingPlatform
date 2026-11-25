
package com.moviestreaming.dao;

import com.moviestreaming.model.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private final Connection connection;

    public ReviewDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Review
    public int addReview(Review review) throws SQLException {

        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }

        String sql = """
                INSERT INTO reviews
                (user_id, movie_id, rating, comment)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getMovieId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating review failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Creating review failed, no ID obtained");
            }
        }
    }

    // Get Review By ID
    public Review getReviewById(int reviewId) throws SQLException {

        if (reviewId <= 0) {
            throw new IllegalArgumentException("Review ID must be positive");
        }

        String sql = """
                SELECT *
                FROM reviews
                WHERE review_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, reviewId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToReview(resultSet);
                }

                return null;
            }
        }
    }

    // Get Review By User And Movie
    public Review getReviewByUserAndMovie(int userId, int movieId)
            throws SQLException {

        if (userId <= 0 || movieId <= 0) {
            throw new IllegalArgumentException("User ID and Movie ID must be positive");
        }

        String sql = """
                SELECT *
                FROM reviews
                WHERE user_id = ? AND movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToReview(resultSet);
                }

                return null;
            }
        }
    }

    // Get Reviews By Movie ID
    public List<Review> getReviewsByMovieId(int movieId) throws SQLException {

        if (movieId <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        String sql = """
                SELECT *
                FROM reviews
                WHERE movie_id = ?
                ORDER BY review_date DESC
                """;

        List<Review> reviews = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    reviews.add(mapResultSetToReview(resultSet));
                }
            }
        }

        return reviews;
    }

    // Get Reviews By User ID
    public List<Review> getReviewsByUserId(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                SELECT *
                FROM reviews
                WHERE user_id = ?
                ORDER BY review_date DESC
                """;

        List<Review> reviews = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    reviews.add(mapResultSetToReview(resultSet));
                }
            }
        }

        return reviews;
    }

    // Get All Reviews
    public List<Review> getAllReviews() throws SQLException {

        String sql = """
                SELECT *
                FROM reviews
                ORDER BY review_date DESC
                """;

        List<Review> reviews = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                reviews.add(mapResultSetToReview(resultSet));
            }
        }

        return reviews;
    }

    // Update Review
    public boolean updateReview(Review review) throws SQLException {

        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }

        if (review.getReviewId() == null || review.getReviewId() <= 0) {
            throw new IllegalArgumentException("Invalid review ID");
        }

        String sql = """
                UPDATE reviews
                SET rating = ?,
                    comment = ?
                WHERE review_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getReviewId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete Review
    public boolean deleteReview(int reviewId) throws SQLException {

        if (reviewId <= 0) {
            throw new IllegalArgumentException("Review ID must be positive");
        }

        String sql = """
                DELETE FROM reviews
                WHERE review_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, reviewId);

            return ps.executeUpdate() > 0;
        }
    }

    // Get Average Rating By Movie ID
    public double getAverageRatingByMovieId(int movieId) throws SQLException {

        if (movieId <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        String sql = """
                SELECT AVG(rating)
                FROM reviews
                WHERE movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                }

                return 0.0;
            }
        }
    }

    // ResultSet Mapping
    private Review mapResultSetToReview(ResultSet resultSet) throws SQLException {

        Integer reviewId = resultSet.getInt("review_id");

        Integer userId = resultSet.getInt("user_id");
        Integer movieId = resultSet.getInt("movie_id");
        int rating = resultSet.getInt("rating");

        String comment = resultSet.getString("comment");

        Timestamp timestamp = resultSet.getTimestamp("review_date");

        return new Review(
                reviewId,
                userId,
                movieId,
                rating,
                comment,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
