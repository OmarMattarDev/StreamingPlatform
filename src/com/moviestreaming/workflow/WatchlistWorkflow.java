
package com.moviestreaming.workflow;

import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.dao.WatchlistDAO;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.User;
import com.moviestreaming.model.Watchlist;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;

public class WatchlistWorkflow {

    private final Connection connection;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;
    private final WatchlistDAO watchlistDAO;

    public WatchlistWorkflow(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.movieDAO = new MovieDAO(connection);
        this.watchlistDAO = new WatchlistDAO(connection);
    }

    public Integer addMovieToWatchlist(
            Integer userId,
            Integer movieId
    ) throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    User user = userDAO.getUserById(userId);

                    if (user == null) {
                        throw new IllegalArgumentException(
                                "User does not exist."
                        );
                    }

                    if (user.getUserStatus() != UserStatus.ACTIVE) {
                        throw new IllegalStateException(
                                "Only active users can use the watchlist."
                        );
                    }

                    Movie movie = movieDAO.getMovieById(movieId);

                    if (movie == null) {
                        throw new IllegalArgumentException(
                                "Movie does not exist."
                        );
                    }

                    Watchlist existingEntry =
                            watchlistDAO.getWatchlistItem(
                                    userId,
                                    movieId
                            );

                    if (existingEntry != null) {
                        throw new IllegalArgumentException(
                                "Movie already exists in the watchlist."
                        );
                    }

                    Watchlist watchlist =
                            new Watchlist(userId, movieId);

                    return watchlistDAO.addToWatchlist(watchlist);
                }
        );
    }

    public boolean removeMovieFromWatchlist(
            Integer userId,
            Integer movieId
    ) throws SQLException {

        validateId(userId, "User ID");
        validateId(movieId, "Movie ID");

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    Watchlist existingEntry =
                            watchlistDAO.getWatchlistItem(
                                    userId,
                                    movieId
                            );

                    if (existingEntry == null) {
                        return false;
                    }

                    return watchlistDAO.removeFromWatchlist(
                            userId,
                            movieId
                    );
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
