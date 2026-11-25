
package com.moviestreaming.workflow;

import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.dao.WatchHistoryDAO;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.User;
import com.moviestreaming.model.WatchHistory;
import com.moviestreaming.model.enums.MovieStatus;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;

public class WatchHistoryWorkflow {

    private final Connection connection;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;
    private final WatchHistoryDAO watchHistoryDAO;

    public WatchHistoryWorkflow(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.movieDAO = new MovieDAO(connection);
        this.watchHistoryDAO = new WatchHistoryDAO(connection);
    }

    public Integer startWatching(
            Integer userId,
            Integer movieId
    ) throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    validateWatchingEligibility(userId, movieId);

                    WatchHistory existingHistory =
                            watchHistoryDAO.getHistoryByUserAndMovie(
                                    userId,
                                    movieId
                            );

                    if (existingHistory != null) {
                        throw new IllegalArgumentException(
                                "Watch history already exists."
                        );
                    }

                    WatchHistory history = new WatchHistory(
                            userId,
                            movieId,
                            0,
                            false
                    );

                    return watchHistoryDAO.addWatchHistory(history);
                }
        );
    }

    public boolean updateWatchingProgress(
            Integer userId,
            Integer movieId,
            Integer watchedSeconds,
            boolean completed
    ) throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        if (watchedSeconds == null || watchedSeconds < 0) {
            throw new IllegalArgumentException(
                    "Watched seconds cannot be negative or null."
            );
        }

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    validateWatchingEligibility(userId, movieId);

                    WatchHistory history =
                            watchHistoryDAO.getHistoryByUserAndMovie(
                                    userId,
                                    movieId
                            );

                    if (history == null) {
                        return false;
                    }

                    history.setWatchedSeconds(watchedSeconds);
                    history.setCompleted(completed);

                    return watchHistoryDAO.updateWatchHistory(history);
                }
        );
    }

    private void validateWatchingEligibility(
            Integer userId,
            Integer movieId
    ) throws SQLException {

        User user = userDAO.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException(
                    "User does not exist."
            );
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active users can watch movies."
            );
        }

        Movie movie = movieDAO.getMovieById(movieId);

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
    }

    private void validateId(Integer id, String fieldName) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}
