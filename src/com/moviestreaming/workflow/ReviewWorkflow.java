
package com.moviestreaming.workflow;

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

public class ReviewWorkflow {

    private final Connection connection;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;
    private final ReviewDAO reviewDAO;

    public ReviewWorkflow(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.movieDAO = new MovieDAO(connection);
        this.reviewDAO = new ReviewDAO(connection);
    }

    public Integer submitReview(Review review) throws SQLException {

        if (review == null) {
            throw new IllegalArgumentException(
                    "Review cannot be null."
            );
        }

        validateId(review.getUserId(), "User ID");
        validateId(review.getMovieId(), "Movie ID");

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    User user = userDAO.getUserById(review.getUserId());

                    if (user == null) {
                        throw new IllegalArgumentException(
                                "User does not exist."
                        );
                    }

                    if (user.getUserStatus() != UserStatus.ACTIVE) {
                        throw new IllegalStateException(
                                "Only active users can submit reviews."
                        );
                    }

                    Movie movie =
                            movieDAO.getMovieById(review.getMovieId());

                    if (movie == null) {
                        throw new IllegalArgumentException(
                                "Movie does not exist."
                        );
                    }

                    if (movie.getMovieStatus() != MovieStatus.AVAILABLE) {
                        throw new IllegalStateException(
                                "Movie is unavailable."
                        );
                    }

                    Review existingReview =
                            reviewDAO.getReviewByUserAndMovie(
                                    review.getUserId(),
                                    review.getMovieId()
                            );

                    if (existingReview != null) {
                        throw new IllegalArgumentException(
                                "User has already reviewed this movie."
                        );
                    }

                    return reviewDAO.addReview(review);
                }
        );
    }

    private void validateId(Integer id, String fieldName) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}
