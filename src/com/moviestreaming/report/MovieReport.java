
package com.moviestreaming.report;

import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.enums.MovieStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class MovieReport {

    private final MovieDAO movieDAO;

    public MovieReport(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.movieDAO = new MovieDAO(connection);
    }

    public int getTotalMovies() throws SQLException {
        return movieDAO.getAllMovies().size();
    }

    public long getAvailableMoviesCount()
            throws SQLException {

        return movieDAO.getAllMovies()
                .stream()
                .filter(movie ->
                        movie.getMovieStatus() == MovieStatus.AVAILABLE
                )
                .count();
    }

    public long getUnavailableMoviesCount()
            throws SQLException {

        return movieDAO.getAllMovies()
                .stream()
                .filter(movie ->
                        movie.getMovieStatus() == MovieStatus.UNAVAILABLE
                )
                .count();
    }

    public double getAverageMovieDuration()
            throws SQLException {

        OptionalDouble average = movieDAO.getAllMovies()
                .stream()
                .mapToInt(Movie::getDurationMinutes)
                .average();

        return average.orElse(0.0);
    }

    public List<Movie> getMoviesSortedByTitle()
            throws SQLException {

        return movieDAO.getAllMovies()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Movie::getTitle,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }

    public List<Movie> getMoviesSortedByReleaseYear()
            throws SQLException {

        return movieDAO.getAllMovies()
                .stream()
                .filter(movie -> movie.getReleaseYear() != null)
                .sorted(
                        Comparator.comparingInt(Movie::getReleaseYear)
                )
                .toList();
    }

    public List<Movie> getLongMovies(int minimumDuration)
            throws SQLException {

        if (minimumDuration <= 0) {
            throw new IllegalArgumentException(
                    "Minimum duration must be greater than zero."
            );
        }

        return movieDAO.getAllMovies()
                .stream()
                .filter(movie ->
                        movie.getDurationMinutes() >= minimumDuration
                )
                .sorted(
                        Comparator.comparingInt(
                                Movie::getDurationMinutes
                        ).reversed()
                )
                .toList();
    }
}
