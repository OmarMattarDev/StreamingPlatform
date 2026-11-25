
package com.moviestreaming.service;

import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.dao.ReviewDAO;
import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.Review;
import com.moviestreaming.model.User;
import com.moviestreaming.model.enums.MovieStatus;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class ReviewService {

    private final Connection connection;
    private final ReviewDAO reviewDAO;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;

    public ReviewService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.reviewDAO = new ReviewDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.movieDAO = new MovieDAO(connection);
    }

    public int addReview(Review review) throws SQLException {
        validateReview(review);

        return executeTransaction(() -> {

            User user = userDAO.getUserById(review.getUserId());

            if (user == null) {
                throw new IllegalArgumentException("User does not exist.");
            }

            if (user.getUserStatus() != UserStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Only active users can add reviews."
                );
            }

            Movie movie = movieDAO.getMovieById(review.getMovieId());

            if (movie == null) {
                throw new IllegalArgumentException("Movie does not exist.");
            }

            if (movie.getMovieStatus() != MovieStatus.AVAILABLE) {
                throw new IllegalArgumentException(
                        "Reviews cannot be added to unavailable movies."
                );
            }

            Review existingReview = reviewDAO.getReviewByUserAndMovie(
                    review.getUserId(),
                    review.getMovieId()
            );

            if (existingReview != null) {
                throw new IllegalArgumentException(
                        "User has already reviewed this movie."
                );
            }

            return reviewDAO.addReview(review);
        });
    }

    public Review getReviewById(Integer reviewId) throws SQLException {
        validateId(reviewId, "Review ID");

        return reviewDAO.getReviewById(reviewId);
    }

    public Review getReviewByUserAndMovie(Integer userId, Integer movieId)
            throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return reviewDAO.getReviewByUserAndMovie(userId, movieId);
    }

    public List<Review> getReviewsByMovie(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return reviewDAO.getReviewsByMovieId(movieId);
    }

    public List<Review> getReviewsByUser(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return reviewDAO.getReviewsByMovieId(userId);
    }

    public List<Review> getAllReviews() throws SQLException {
        return reviewDAO.getAllReviews();
    }

    public boolean updateReview(Review review) throws SQLException {
        validateReview(review);
        validateId(review.getReviewId(), "Review ID");

        return executeTransaction(() -> {

            Review existingReview =
                    reviewDAO.getReviewById(review.getReviewId());

            if (existingReview == null) {
                return false;
            }

            if (!existingReview.getUserId().equals(review.getUserId()) ||
                    !existingReview.getMovieId().equals(review.getMovieId())) {
                throw new IllegalArgumentException(
                        "Review user and movie cannot be changed."
                );
            }

            return reviewDAO.updateReview(review);
        });
    }

    public boolean deleteReview(Integer reviewId) throws SQLException {
        validateId(reviewId, "Review ID");

        return executeTransaction(() -> reviewDAO.deleteReview(reviewId));
    }

    public double getAverageRatingByMovie(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return reviewDAO.getAverageRatingByMovieId(movieId);
    }

    public List<Review> getReviewsSortedByRating(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return getReviewsByMovie(movieId).stream()
                .sorted(Comparator.comparingInt(Review::getRating).reversed())
                .toList();
    }

    public List<Review> getPositiveReviews(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return getReviewsByMovie(movieId).stream()
                .filter(Review::isPositiveReview)
                .toList();
    }

    private void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null.");
        }

        validateId(review.getUserId(), "User ID");
        validateId(review.getMovieId(), "Movie ID");
    }

    private void validateId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }

    private <T> T executeTransaction(SqlOperation<T> operation)
            throws SQLException {

        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            T result = operation.execute();

            connection.commit();

            return result;

        } catch (SQLException | RuntimeException exception) {
            connection.rollback();

            throw exception;

        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @FunctionalInterface
    private interface SqlOperation<T> {
        T execute() throws SQLException;
    }
}
