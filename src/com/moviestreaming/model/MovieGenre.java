
package com.moviestreaming.model;

import java.util.Objects;

public class MovieGenre {

    // Fields
    private Integer movieId;
    private Integer genreId;

    // Constructor
    public MovieGenre(Integer movieId, Integer genreId) {

        setMovieId(movieId);
        setGenreId(genreId);
    }

    // Getters
    public Integer getMovieId() {
        return movieId;
    }

    public Integer getGenreId() {
        return genreId;
    }

    // Setters
    public void setMovieId(Integer movieId) {

        if (movieId == null || movieId <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        this.movieId = movieId;
    }

    public void setGenreId(Integer genreId) {

        if (genreId == null || genreId <= 0) {
            throw new IllegalArgumentException("Invalid genre ID.");
        }

        this.genreId = genreId;
    }

    // Business Methods
    public boolean belongsToMovie(Integer movieId) {

        return movieId != null
                && this.movieId.equals(movieId);
    }

    public boolean belongsToGenre(Integer genreId) {

        return genreId != null
                && this.genreId.equals(genreId);
    }

    // Object Methods
    @Override
    public String toString() {
        return "MovieGenre{" +
                "movieId=" + movieId +
                ", genreId=" + genreId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MovieGenre)) {
            return false;
        }

        MovieGenre movieGenre = (MovieGenre) obj;

        return movieId.equals(movieGenre.movieId)
                && genreId.equals(movieGenre.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, genreId);
    }
}
