
package com.moviestreaming.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Watchlist {

    // Fields
    private Integer watchlistId;
    private Integer userId;
    private Integer movieId;
    private LocalDateTime addedAt;

    // Constructor for new watchlist entries
    public Watchlist(Integer userId, Integer movieId) {

        setUserId(userId);
        setMovieId(movieId);

        this.addedAt = LocalDateTime.now();
    }

    // Constructor for database mapping
    public Watchlist(Integer watchlistId, Integer userId,
                     Integer movieId, LocalDateTime addedAt) {

        if (watchlistId == null || watchlistId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid watchlist ID.");
        }

        this.watchlistId = watchlistId;

        setUserId(userId);
        setMovieId(movieId);

        validateAddedAt(addedAt);
        this.addedAt = addedAt;
    }

    // Getters
    public Integer getWatchlistId() {
        return watchlistId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
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

    // Validation Methods
    private void validateAddedAt(LocalDateTime date) {

        if (date == null || date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Invalid watchlist addition date.");
        }
    }

    // Business Methods
    public boolean belongsToUser(Integer userId) {

        return userId != null
                && this.userId.equals(userId);
    }

    public boolean containsMovie(Integer movieId) {

        return movieId != null
                && this.movieId.equals(movieId);
    }

    // Object Methods
    @Override
    public String toString() {
        return "Watchlist{" +
                "watchlistId=" + watchlistId +
                ", userId=" + userId +
                ", movieId=" + movieId +
                ", addedAt=" + addedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Watchlist)) {
            return false;
        }

        Watchlist watchlist = (Watchlist) obj;

        return watchlistId != null
                && watchlistId.equals(watchlist.watchlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(watchlistId);
    }
}
