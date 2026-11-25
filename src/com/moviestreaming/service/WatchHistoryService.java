
package com.moviestreaming.service;

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
import java.util.Comparator;
import java.util.List;

public class WatchHistoryService {

    private final Connection connection;
    private final WatchHistoryDAO watchHistoryDAO;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;

    public WatchHistoryService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.watchHistoryDAO = new WatchHistoryDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.movieDAO = new MovieDAO(connection);
    }

    public int addWatchHistory(WatchHistory history) throws SQLException {
        validateHistory(history);

        return executeTransaction(() -> {

            validateUserAndMovie(
                    history.getUserId(),
                    history.getMovieId()
            );

            WatchHistory existingHistory =
                    watchHistoryDAO.getHistoryByUserAndMovie(
                            history.getUserId(),
                            history.getMovieId()
                    );

            if (existingHistory != null) {
                throw new IllegalArgumentException(
                        "Watch history already exists for this user and movie."
                );
            }

            return watchHistoryDAO.addWatchHistory(history);
        });
    }

    public WatchHistory getWatchHistoryById(Integer historyId)
            throws SQLException {

        validateId(historyId, "History ID");

        return watchHistoryDAO.getHistoryById(historyId);
    }

    public WatchHistory getWatchHistoryByUserAndMovie(
            Integer userId, Integer movieId) throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return watchHistoryDAO.getHistoryByUserAndMovie(
                userId, movieId
        );
    }

    public List<WatchHistory> getWatchHistoryByUser(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return watchHistoryDAO.getHistoryByUserId(userId);
    }

    public List<WatchHistory> getAllWatchHistory() throws SQLException {
        return watchHistoryDAO.getAllHistory();
    }

    public boolean updateWatchHistory(WatchHistory history)
            throws SQLException {

        validateHistory(history);
        validateId(history.getHistoryId(), "History ID");

        return executeTransaction(() -> {

            WatchHistory existingHistory =
                    watchHistoryDAO.getHistoryById(history.getHistoryId());

            if (existingHistory == null) {
                return false;
            }

            if (!existingHistory.getUserId().equals(history.getUserId()) ||
                    !existingHistory.getMovieId().equals(history.getMovieId())) {
                throw new IllegalArgumentException(
                        "User ID and Movie ID cannot be changed."
                );
            }

            return watchHistoryDAO.updateWatchHistory(history);
        });
    }

    public boolean updateWatchProgress(
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

        return executeTransaction(() -> {

            validateUserAndMovie(userId, movieId);

            WatchHistory history =
                    watchHistoryDAO.getHistoryByUserAndMovie(
                            userId, movieId
                    );

            if (history == null) {
                return false;
            }

            history.setWatchedSeconds(watchedSeconds);
            history.setCompleted(completed);

            return watchHistoryDAO.updateWatchHistory(history);
        });
    }

    public boolean deleteWatchHistory(Integer historyId)
            throws SQLException {

        validateId(historyId, "History ID");

        return executeTransaction(
                () -> watchHistoryDAO.deleteHistory(historyId)
        );
    }

    public boolean deleteUserWatchHistory(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return executeTransaction(
                () -> watchHistoryDAO.deleteUserHistory(userId)
        );
    }

    public List<WatchHistory> getCompletedMovies(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return getWatchHistoryByUser(userId).stream()
                .filter(WatchHistory::isCompleted)
                .toList();
    }

    public List<WatchHistory> getIncompleteMovies(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return getWatchHistoryByUser(userId).stream()
                .filter(history -> !history.isCompleted())
                .toList();
    }

    public List<WatchHistory> getWatchHistorySortedByProgress(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return getWatchHistoryByUser(userId).stream()
                .sorted(Comparator.comparingInt(
                        WatchHistory::getWatchedSeconds
                ).reversed())
                .toList();
    }

    private void validateUserAndMovie(Integer userId, Integer movieId)
            throws SQLException {

        User user = userDAO.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist.");
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active users can watch movies."
            );
        }

        Movie movie = movieDAO.getMovieById(movieId);

        if (movie == null) {
            throw new IllegalArgumentException("Movie does not exist.");
        }

        if (movie.getMovieStatus() != MovieStatus.AVAILABLE) {
            throw new IllegalArgumentException("Movie is unavailable.");
        }
    }

    private void validateHistory(WatchHistory history) {
        if (history == null) {
            throw new IllegalArgumentException(
                    "Watch history cannot be null."
            );
        }

        validateId(history.getUserId(), "User ID");
        validateId(history.getMovieId(), "Movie ID");
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
