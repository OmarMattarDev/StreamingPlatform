
package com.moviestreaming.service;

import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.dao.WatchlistDAO;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.User;
import com.moviestreaming.model.Watchlist;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class WatchlistService {

    private final Connection connection;
    private final WatchlistDAO watchlistDAO;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;

    public WatchlistService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.watchlistDAO = new WatchlistDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.movieDAO = new MovieDAO(connection);
    }

    public int addToWatchlist(Integer userId, Integer movieId)
            throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return executeTransaction(() -> {

            User user = userDAO.getUserById(userId);

            if (user == null) {
                throw new IllegalArgumentException("User does not exist.");
            }

            if (user.getUserStatus() != UserStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Only active users can use the watchlist."
                );
            }

            Movie movie = movieDAO.getMovieById(movieId);

            if (movie == null) {
                throw new IllegalArgumentException("Movie does not exist.");
            }

            Watchlist existingEntry =
                    watchlistDAO.getWatchlistItem(userId, movieId);

            if (existingEntry != null) {
                throw new IllegalArgumentException(
                        "Movie already exists in this watchlist."
                );
            }

            Watchlist watchlist = new Watchlist(userId, movieId);

            return watchlistDAO.addToWatchlist(watchlist);
        });
    }

    public Watchlist getWatchlistById(Integer watchlistId)
            throws SQLException {

        validateId(watchlistId, "Watchlist ID");

        return watchlistDAO.getWatchlistItemById(watchlistId);
    }

    public Watchlist getWatchlistByUserAndMovie(
            Integer userId, Integer movieId) throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return watchlistDAO.getWatchlistItem(userId, movieId);
    }

    public List<Watchlist> getWatchlistByUser(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return watchlistDAO.getWatchlistByUserId(userId);
    }

    public List<Watchlist> getAllWatchlistEntries() throws SQLException {
        return watchlistDAO.getAllWatchlistItems();
    }

    public boolean isMovieInWatchlist(Integer userId, Integer movieId)
            throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return watchlistDAO.isMovieInWatchlist(userId, movieId);
    }

    public boolean removeFromWatchlist(Integer userId, Integer movieId)
            throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return executeTransaction(
                () -> watchlistDAO.removeFromWatchlist(userId, movieId)
        );
    }

    public boolean removeWatchlistEntry(Integer watchlistId)
            throws SQLException {

        validateId(watchlistId, "Watchlist ID");

        return executeTransaction(
                () -> watchlistDAO.removeWatchlistItem(watchlistId)
        );
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
