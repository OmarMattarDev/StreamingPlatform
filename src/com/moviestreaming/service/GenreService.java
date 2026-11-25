
package com.moviestreaming.service;

import com.moviestreaming.dao.GenreDAO;
import com.moviestreaming.model.Genre;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class GenreService {

    private final Connection connection;
    private final GenreDAO genreDAO;

    public GenreService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.genreDAO = new GenreDAO(connection);
    }

    public int addGenre(Genre genre) throws SQLException {
        validateGenre(genre);

        if (genreDAO.getGenreByName(genre.getGenreName()) != null) {
            throw new IllegalArgumentException("Genre name already exists.");
        }

        return executeTransaction(() -> genreDAO.addGenre(genre));
    }

    public Genre getGenreById(Integer genreId) throws SQLException {
        validateId(genreId, "Genre ID");

        return genreDAO.getGenreById(genreId);
    }

    public Genre getGenreByName(String genreName) throws SQLException {
        validateString(genreName, "Genre name");

        return genreDAO.getGenreByName(genreName.trim());
    }

    public List<Genre> getAllGenres() throws SQLException {
        return genreDAO.getAllGenres();
    }

    public boolean updateGenre(Genre genre) throws SQLException {
        validateGenre(genre);
        validateId(genre.getGenreId(), "Genre ID");

        return executeTransaction(() -> {
            Genre existingGenre = genreDAO.getGenreById(genre.getGenreId());

            if (existingGenre == null) {
                return false;
            }

            Genre genreWithSameName =
                    genreDAO.getGenreByName(genre.getGenreName());

            if (genreWithSameName != null &&
                    !genreWithSameName.getGenreId().equals(genre.getGenreId())) {
                throw new IllegalArgumentException(
                        "Genre name already exists."
                );
            }

            return genreDAO.updateGenre(genre);
        });
    }

    public boolean deleteGenre(Integer genreId) throws SQLException {
        validateId(genreId, "Genre ID");

        return executeTransaction(() -> genreDAO.deleteGenre(genreId));
    }

    public List<Genre> getGenresSortedByName() throws SQLException {
        return getAllGenres().stream()
                .sorted(Comparator.comparing(Genre::getGenreName))
                .toList();
    }

    private void validateGenre(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null.");
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
