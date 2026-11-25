
package com.moviestreaming.dao;

import com.moviestreaming.model.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    private final Connection connection;

    public GenreDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Genre
    public int addGenre(Genre genre) throws SQLException {

        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }

        String sql = """
                INSERT INTO genres
                (genre_name, description)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, genre.getGenreName());
            ps.setString(2, genre.getDescription());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating genre failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Creating genre failed, no ID obtained");
            }
        }
    }

    // Get Genre By ID
    public Genre getGenreById(int genreId) throws SQLException {

        if (genreId <= 0) {
            throw new IllegalArgumentException("Genre ID must be positive");
        }

        String sql = """
                SELECT *
                FROM genres
                WHERE genre_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, genreId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToGenre(resultSet);
                }

                return null;
            }
        }
    }

    // Get Genre By Name
    public Genre getGenreByName(String genreName) throws SQLException {

        if (genreName == null || genreName.isBlank()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty");
        }

        String sql = """
                SELECT *
                FROM genres
                WHERE genre_name = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, genreName.trim());

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToGenre(resultSet);
                }

                return null;
            }
        }
    }

    // Get All Genres
    public List<Genre> getAllGenres() throws SQLException {

        String sql = """
                SELECT *
                FROM genres
                ORDER BY genre_name
                """;

        List<Genre> genres = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                genres.add(mapResultSetToGenre(resultSet));
            }
        }

        return genres;
    }

    // Update Genre
    public boolean updateGenre(Genre genre) throws SQLException {

        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }

        if (genre.getGenreId() == null || genre.getGenreId() <= 0) {
            throw new IllegalArgumentException("Invalid genre ID");
        }

        String sql = """
                UPDATE genres
                SET genre_name = ?,
                    description = ?
                WHERE genre_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, genre.getGenreName());
            ps.setString(2, genre.getDescription());
            ps.setInt(3, genre.getGenreId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete Genre
    public boolean deleteGenre(int genreId) throws SQLException {

        if (genreId <= 0) {
            throw new IllegalArgumentException("Genre ID must be positive");
        }

        String sql = """
                DELETE FROM genres
                WHERE genre_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, genreId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private Genre mapResultSetToGenre(ResultSet resultSet) throws SQLException {

        Integer genreId = resultSet.getInt("genre_id");

        String genreName = resultSet.getString("genre_name");
        String description = resultSet.getString("description");

        return new Genre(
                genreId,
                genreName,
                description
        );
    }
}
