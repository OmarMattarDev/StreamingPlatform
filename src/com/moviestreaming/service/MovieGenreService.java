
package com.moviestreaming.service;

import com.moviestreaming.dao.GenreDAO;
import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.dao.MovieGenreDAO;
import com.moviestreaming.model.Genre;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.MovieGenre;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MovieGenreService {

    private final Connection connection;
    private final MovieGenreDAO movieGenreDAO;
    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;

    public MovieGenreService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.movieGenreDAO = new MovieGenreDAO(connection);
        this.movieDAO = new MovieDAO(connection);
        this.genreDAO = new GenreDAO(connection);
    }

    public boolean addGenreToMovie(Integer movieId, Integer genreId)
            throws SQLException {

        validateId(movieId, "Movie ID");
        validateId(genreId, "Genre ID");

        return executeTransaction(() -> {

            Movie movie = movieDAO.getMovieById(movieId);

            if (movie == null) {
                throw new IllegalArgumentException("Movie does not exist.");
            }

            Genre genre = genreDAO.getGenreById(genreId);

            if (genre == null) {
                throw new IllegalArgumentException("Genre does not exist.");
            }

            MovieGenre existingRelation =
                    movieGenreDAO.getMovieGenre(movieId, genreId);

            if (existingRelation != null) {
                throw new IllegalArgumentException(
                        "This genre is already assigned to the movie."
                );
            }

            MovieGenre movieGenre = new MovieGenre(movieId, genreId);

            return movieGenreDAO.addGenreToMovie(movieGenre);
        });
    }

    public MovieGenre getMovieGenre(Integer movieId, Integer genreId)
            throws SQLException {

        validateId(movieId, "Movie ID");
        validateId(genreId, "Genre ID");

        return movieGenreDAO.getMovieGenre(movieId, genreId);
    }

    public List<MovieGenre> getGenresByMovie(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return movieGenreDAO.getGenresByMovieId(movieId);
    }

    public List<MovieGenre> getMoviesByGenre(Integer genreId)
            throws SQLException {

        validateId(genreId, "Genre ID");

        return movieGenreDAO.getGenresByMovieId(genreId);
    }

    public boolean removeGenreFromMovie(Integer movieId, Integer genreId)
            throws SQLException {

        validateId(movieId, "Movie ID");
        validateId(genreId, "Genre ID");

        return executeTransaction(
                () -> movieGenreDAO.removeGenreFromMovie(movieId, genreId)
        );
    }

    public boolean removeAllGenresFromMovie(Integer movieId)
            throws SQLException {

        validateId(movieId, "Movie ID");

        return executeTransaction(
                () -> movieGenreDAO.removeAllGenresFromMovie(movieId)
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
