
package com.moviestreaming.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class WatchHistory {

    // Fields
    private Integer historyId;
    private Integer userId;
    private Integer movieId;
    private Integer watchedSeconds;
    private boolean completed;
    private LocalDateTime lastWatchedAt;

    // Constructor for new watch history
    public WatchHistory(Integer userId, Integer movieId,
                        Integer watchedSeconds, boolean completed) {

        setUserId(userId);
        setMovieId(movieId);
        setWatchedSeconds(watchedSeconds);
        setCompleted(completed);

        this.lastWatchedAt = LocalDateTime.now();
    }

    // Constructor for database mapping
    public WatchHistory(Integer historyId, Integer userId,
                        Integer movieId, Integer watchedSeconds,
                        boolean completed, LocalDateTime lastWatchedAt) {

        if (historyId == null || historyId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid watch history ID.");
        }

        this.historyId = historyId;

        setUserId(userId);
        setMovieId(movieId);
        setWatchedSeconds(watchedSeconds);
        setCompleted(completed);

        validateLastWatchedAt(lastWatchedAt);
        this.lastWatchedAt = lastWatchedAt;
    }

    // Getters
    public Integer getHistoryId() {
        return historyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public Integer getWatchedSeconds() {
        return watchedSeconds;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }

    // Setters
    public void setUserId(Integer userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        this.userId = userId;
    }

    public void setMovieId(Integer movieId) {

        if (movieId == null || movieId <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        this.movieId = movieId;
    }

    public void setWatchedSeconds(Integer watchedSeconds) {

        if (watchedSeconds == null || watchedSeconds < 0) {
            throw new IllegalArgumentException(
                    "Watched seconds cannot be negative or null.");
        }

        this.watchedSeconds = watchedSeconds;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Validation Methods
    private void validateLastWatchedAt(LocalDateTime date) {

        if (date == null || date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Invalid last watched date.");
        }
    }

    // Business Methods
    public void updateProgress(Integer additionalSeconds) {

        if (additionalSeconds == null || additionalSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Additional watched seconds must be positive.");
        }

        watchedSeconds = Math.addExact(
                watchedSeconds, additionalSeconds);

        lastWatchedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {

        completed = true;
        lastWatchedAt = LocalDateTime.now();
    }

    public void resetProgress() {

        watchedSeconds = 0;
        completed = false;
        lastWatchedAt = LocalDateTime.now();
    }

    public int getWatchedMinutes() {
        return watchedSeconds / 60;
    }

    // Object Methods
    @Override
    public String toString() {
        return "WatchHistory{" +
                "historyId=" + historyId +
                ", userId=" + userId +
                ", movieId=" + movieId +
                ", watchedSeconds=" + watchedSeconds +
                ", completed=" + completed +
                ", lastWatchedAt=" + lastWatchedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof WatchHistory)) {
            return false;
        }

        WatchHistory history = (WatchHistory) obj;

        return historyId != null
                && historyId.equals(history.historyId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(historyId);
    }
}
