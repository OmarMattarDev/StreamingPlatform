
package com.moviestreaming.dao;

import com.moviestreaming.model.Watchlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    private final Connection connection;

    public WatchlistDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add To Watchlist
    public int addToWatchlist(Watchlist watchlist) throws SQLException {

        if (watchlist == null) {
            throw new IllegalArgumentException("Watchlist cannot be null");
        }

        String sql = """
                INSERT INTO watchlist
                (user_id, movie_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, watchlist.getUserId());
            ps.setInt(2, watchlist.getMovieId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding movie to watchlist failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Adding movie to watchlist failed, no ID obtained");
            }
        }
    }

    // Get Watchlist Item By ID
    public Watchlist getWatchlistItemById(int watchlistId) throws SQLException {

        if (watchlistId <= 0) {
            throw new IllegalArgumentException("Watchlist ID must be positive");
        }

        String sql = """
                SELECT *
                FROM watchlist
                WHERE watchlist_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, watchlistId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToWatchlist(resultSet);
                }

                return null;
            }
        }
    }

    // Get Watchlist Item By User And Movie
    public Watchlist getWatchlistItem(int userId, int movieId) throws SQLException {

        if (userId <= 0 || movieId <= 0) {
            throw new IllegalArgumentException("User ID and Movie ID must be positive");
        }

        String sql = """
                SELECT *
                FROM watchlist
                WHERE user_id = ? AND movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToWatchlist(resultSet);
                }

                return null;
            }
        }
    }

    // Get Watchlist By User ID
    public List<Watchlist> getWatchlistByUserId(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                SELECT *
                FROM watchlist
                WHERE user_id = ?
                ORDER BY added_at DESC
                """;

        List<Watchlist> watchlist = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    watchlist.add(mapResultSetToWatchlist(resultSet));
                }
            }
        }

        return watchlist;
    }

    // Get All Watchlist Items
    public List<Watchlist> getAllWatchlistItems() throws SQLException {

        String sql = """
                SELECT *
                FROM watchlist
                ORDER BY added_at DESC
                """;

        List<Watchlist> watchlist = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                watchlist.add(mapResultSetToWatchlist(resultSet));
            }
        }

        return watchlist;
    }

    // Check If Movie Exists In Watchlist
    public boolean isMovieInWatchlist(int userId, int movieId) throws SQLException {

        if (userId <= 0 || movieId <= 0) {
            throw new IllegalArgumentException("User ID and Movie ID must be positive");
        }

        String sql = """
                SELECT 1
                FROM watchlist
                WHERE user_id = ? AND movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Remove From Watchlist
    public boolean removeFromWatchlist(int userId, int movieId) throws SQLException {

        if (userId <= 0 || movieId <= 0) {
            throw new IllegalArgumentException("User ID and Movie ID must be positive");
        }

        String sql = """
                DELETE FROM watchlist
                WHERE user_id = ? AND movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, movieId);

            return ps.executeUpdate() > 0;
        }
    }

    // Remove Watchlist Item By ID
    public boolean removeWatchlistItem(int watchlistId) throws SQLException {

        if (watchlistId <= 0) {
            throw new IllegalArgumentException("Watchlist ID must be positive");
        }

        String sql = """
                DELETE FROM watchlist
                WHERE watchlist_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, watchlistId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private Watchlist mapResultSetToWatchlist(ResultSet resultSet)
            throws SQLException {

        Integer watchlistId = resultSet.getInt("watchlist_id");

        Integer userId = resultSet.getInt("user_id");
        Integer movieId = resultSet.getInt("movie_id");

        Timestamp timestamp = resultSet.getTimestamp("added_at");

        return new Watchlist(
                watchlistId,
                userId,
                movieId,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
