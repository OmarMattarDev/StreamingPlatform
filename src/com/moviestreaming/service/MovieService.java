
package com.moviestreaming.service;

import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.enums.MovieStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class MovieService {

    private final Connection connection;
    private final MovieDAO movieDAO;

    public MovieService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.movieDAO = new MovieDAO(connection);
    }

    public int addMovie(Movie movie) throws SQLException {
        validateMovie(movie);

        return executeTransaction(() -> movieDAO.addMovie(movie));
    }

    public Movie getMovieById(Integer movieId) throws SQLException {
        validateId(movieId, "Movie ID");

        return movieDAO.getMovieById(movieId);
    }

    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.getAllMovies();
    }

    public List<Movie> searchMoviesByTitle(String title)
            throws SQLException {

        validateString(title, "Movie title");

        return movieDAO.searchMoviesByTitle(title.trim());
    }

    public List<Movie> getMoviesByStatus(MovieStatus status)
            throws SQLException {

        if (status == null) {
            throw new IllegalArgumentException("Movie status cannot be null.");
        }

        return movieDAO.getMoviesByStatus(status);
    }

    public boolean updateMovie(Movie movie) throws SQLException {
        validateMovie(movie);
        validateId(movie.getMovieId(), "Movie ID");

        return executeTransaction(() -> {
            Movie existingMovie = movieDAO.getMovieById(movie.getMovieId());

            if (existingMovie == null) {
                return false;
            }

            return movieDAO.updateMovie(movie);
        });
    }

    public boolean deleteMovie(Integer movieId) throws SQLException {
        validateId(movieId, "Movie ID");

        return executeTransaction(() -> movieDAO.deleteMovie(movieId));
    }

    public boolean makeMovieAvailable(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return executeTransaction(() -> {
            Movie movie = movieDAO.getMovieById(movieId);

            if (movie == null) {
                return false;
            }

            movie.makeAvailable();

            return movieDAO.updateMovie(movie);
        });
    }

    public boolean makeMovieUnavailable(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return executeTransaction(() -> {
            Movie movie = movieDAO.getMovieById(movieId);

            if (movie == null) {
                return false;
            }

            movie.makeUnavailable();

            return movieDAO.updateMovie(movie);
        });
    }

    public List<Movie> getAvailableMovies() throws SQLException {
        return getMoviesByStatus(MovieStatus.AVAILABLE);
    }

    public List<Movie> getUnavailableMovies() throws SQLException {
        return getMoviesByStatus(MovieStatus.UNAVAILABLE);
    }

    public List<Movie> getMoviesSortedByTitle() throws SQLException {
        return getAllMovies().stream()
                .sorted(Comparator.comparing(Movie::getTitle))
                .toList();
    }

    public List<Movie> getMoviesSortedByReleaseYear() throws SQLException {
        return getAllMovies().stream()
                .filter(movie -> movie.getReleaseYear() != null)
                .sorted(Comparator.comparing(Movie::getReleaseYear))
                .toList();
    }

    public List<Movie> getLongMovies() throws SQLException {
        return getAllMovies().stream()
                .filter(Movie::isLongMovie)
                .toList();
    }

    private void validateMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null.");
        }
    }

    private void validateId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }

    private void validateString(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
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
