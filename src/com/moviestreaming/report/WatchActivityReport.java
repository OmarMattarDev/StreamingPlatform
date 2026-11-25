
package com.moviestreaming.report;

import com.moviestreaming.dao.WatchHistoryDAO;
import com.moviestreaming.model.WatchHistory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WatchActivityReport {

    private final WatchHistoryDAO watchHistoryDAO;

    public WatchActivityReport(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.watchHistoryDAO = new WatchHistoryDAO(connection);
    }

    public int getTotalWatchRecords() throws SQLException {
        return watchHistoryDAO.getAllHistory().size();
    }

    public long getCompletedMoviesCount()
            throws SQLException {

        return watchHistoryDAO.getAllHistory()
                .stream()
                .filter(WatchHistory::isCompleted)
                .count();
    }

    public long getIncompleteMoviesCount()
            throws SQLException {

        return watchHistoryDAO.getAllHistory()
                .stream()
                .filter(history -> !history.isCompleted())
                .count();
    }

    public double getCompletionRate()
            throws SQLException {

        List<WatchHistory> histories =
                watchHistoryDAO.getAllHistory();

        if (histories.isEmpty()) {
            return 0.0;
        }

        long completedCount = histories.stream()
                .filter(WatchHistory::isCompleted)
                .count();

        return (completedCount * 100.0) / histories.size();
    }

    public Map<Integer, Long> getTotalWatchSecondsByUser()
            throws SQLException {

        return watchHistoryDAO.getAllHistory()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                WatchHistory::getUserId,
                                Collectors.summingLong(
                                        WatchHistory::getWatchedSeconds
                                )
                        )
                );
    }

    public Map<Integer, Long> getWatchRecordsCountByUser()
            throws SQLException {

        return watchHistoryDAO.getAllHistory()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                WatchHistory::getUserId,
                                Collectors.counting()
                        )
                );
    }

    public List<WatchHistory> getMostRecentlyWatched()
            throws SQLException {

        return watchHistoryDAO.getAllHistory()
                .stream()
                .sorted(
                        Comparator.comparing(
                                WatchHistory::getLastWatchedAt
                        ).reversed()
                )
                .toList();
    }
}
