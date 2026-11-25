
package com.moviestreaming.dao;

import com.moviestreaming.model.WatchHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WatchHistoryDAO {

    private final Connection connection;

    public WatchHistoryDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Watch History
    public int addWatchHistory(WatchHistory history) throws SQLException {

        if (history == null) {
            throw new IllegalArgumentException("Watch history cannot be null");
        }

        String sql = """
                INSERT INTO watch_history
                (user_id, movie_id, watched_seconds, completed)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, history.getUserId());
            ps.setInt(2, history.getMovieId());
            ps.setInt(3, history.getWatchedSeconds());
            ps.setBoolean(4, history.isCompleted());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating watch history failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Creating watch history failed, no ID obtained");
            }
        }
    }

    // Get Watch History By ID
    public WatchHistory getHistoryById(int historyId) throws SQLException {

        if (historyId <= 0) {
            throw new IllegalArgumentException("History ID must be positive");
        }

        String sql = """
                SELECT *
                FROM watch_history
                WHERE history_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, historyId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToWatchHistory(resultSet);
                }

                return null;
            }
        }
    }

    // Get History By User And Movie
    public WatchHistory getHistoryByUserAndMovie(int userId, int movieId)
            throws SQLException {

        if (userId <= 0 || movieId <= 0) {
            throw new IllegalArgumentException("User ID and Movie ID must be positive");
        }

        String sql = """
                SELECT *
                FROM watch_history
                WHERE user_id = ? AND movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToWatchHistory(resultSet);
                }

                return null;
            }
        }
    }

    // Get History By User ID
    public List<WatchHistory> getHistoryByUserId(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                SELECT *
                FROM watch_history
                WHERE user_id = ?
                ORDER BY last_watched_at DESC
                """;

        List<WatchHistory> historyList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    historyList.add(mapResultSetToWatchHistory(resultSet));
                }
            }
        }

        return historyList;
    }

    // Get All History
    public List<WatchHistory> getAllHistory() throws SQLException {

        String sql = """
                SELECT *
                FROM watch_history
                ORDER BY last_watched_at DESC
                """;

        List<WatchHistory> historyList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                historyList.add(mapResultSetToWatchHistory(resultSet));
            }
        }

        return historyList;
    }

    // Update Watch History
    public boolean updateWatchHistory(WatchHistory history) throws SQLException {

        if (history == null) {
            throw new IllegalArgumentException("Watch history cannot be null");
        }

        if (history.getHistoryId() == null || history.getHistoryId() <= 0) {
            throw new IllegalArgumentException("Invalid history ID");
        }

        String sql = """
                UPDATE watch_history
                SET watched_seconds = ?,
                    completed = ?
                WHERE history_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, history.getWatchedSeconds());
            ps.setBoolean(2, history.isCompleted());
            ps.setInt(3, history.getHistoryId());

            return ps.executeUpdate() > 0;
        }
    }

    // Update Watch Progress
    public boolean updateWatchProgress(int userId, int movieId,
                                       int watchedSeconds, boolean completed)
            throws SQLException {

        if (userId <= 0 || movieId <= 0) {
            throw new IllegalArgumentException("User ID and Movie ID must be positive");
        }

        if (watchedSeconds < 0) {
            throw new IllegalArgumentException("Watched seconds cannot be negative");
        }

        String sql = """
                UPDATE watch_history
                SET watched_seconds = ?,
                    completed = ?
                WHERE user_id = ? AND movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, watchedSeconds);
            ps.setBoolean(2, completed);
            ps.setInt(3, userId);
            ps.setInt(4, movieId);

            return ps.executeUpdate() > 0;
        }
    }

    // Delete History
    public boolean deleteHistory(int historyId) throws SQLException {

        if (historyId <= 0) {
            throw new IllegalArgumentException("History ID must be positive");
        }

        String sql = """
                DELETE FROM watch_history
                WHERE history_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, historyId);

            return ps.executeUpdate() > 0;
        }
    }

    // Delete User History
    public boolean deleteUserHistory(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                DELETE FROM watch_history
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private WatchHistory mapResultSetToWatchHistory(ResultSet resultSet)
            throws SQLException {

        Integer historyId = resultSet.getInt("history_id");

        Integer userId = resultSet.getInt("user_id");
        Integer movieId = resultSet.getInt("movie_id");

        int watchedSeconds = resultSet.getInt("watched_seconds");
        boolean completed = resultSet.getBoolean("completed");

        Timestamp timestamp = resultSet.getTimestamp("last_watched_at");

        return new WatchHistory(
                historyId,
                userId,
                movieId,
                watchedSeconds,
                completed,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
