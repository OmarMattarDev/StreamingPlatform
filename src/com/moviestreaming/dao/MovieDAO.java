
package com.moviestreaming.dao;

import com.moviestreaming.model.Movie;
import com.moviestreaming.model.enums.MovieStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private final Connection connection;

    public MovieDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Movie
    public int addMovie(Movie movie) throws SQLException {

        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        String sql = """
                INSERT INTO movies
                (title, description, release_year, duration_minutes,
                 age_rating, language, country, director, movie_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getDescription());

            if (movie.getReleaseYear() == null) {
                ps.setNull(3, Types.SMALLINT);
            } else {
                ps.setInt(3, movie.getReleaseYear());
            }

            ps.setInt(4, movie.getDurationMinutes());
            ps.setString(5, movie.getAgeRating());
            ps.setString(6, movie.getLanguage());
            ps.setString(7, movie.getCountry());
            ps.setString(8, movie.getDirector());
            ps.setString(9, movie.getMovieStatus().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating movie failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Creating movie failed, no ID obtained");
            }
        }
    }

    // Get Movie By ID
    public Movie getMovieById(int movieId) throws SQLException {

        if (movieId <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        String sql = """
                SELECT *
                FROM movies
                WHERE movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToMovie(resultSet);
                }

                return null;
            }
        }
    }

    // Get All Movies
    public List<Movie> getAllMovies() throws SQLException {

        String sql = """
                SELECT *
                FROM movies
                ORDER BY movie_id
                """;

        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                movies.add(mapResultSetToMovie(resultSet));
            }
        }

        return movies;
    }

    // Search Movies By Title
    public List<Movie> searchMoviesByTitle(String title) throws SQLException {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or empty");
        }

        String sql = """
                SELECT *
                FROM movies
                WHERE title LIKE ?
                ORDER BY title
                """;

        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + title.trim() + "%");

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    movies.add(mapResultSetToMovie(resultSet));
                }
            }
        }

        return movies;
    }

    // Get Movies By Status
    public List<Movie> getMoviesByStatus(MovieStatus status) throws SQLException {

        if (status == null) {
            throw new IllegalArgumentException("Movie status cannot be null");
        }

        String sql = """
                SELECT *
                FROM movies
                WHERE movie_status = ?
                """;

        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    movies.add(mapResultSetToMovie(resultSet));
                }
            }
        }

        return movies;
    }

    // Update Movie
    public boolean updateMovie(Movie movie) throws SQLException {

        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        if (movie.getMovieId() == null || movie.getMovieId() <= 0) {
            throw new IllegalArgumentException("Invalid movie ID");
        }

        String sql = """
                UPDATE movies
                SET title = ?,
                    description = ?,
                    release_year = ?,
                    duration_minutes = ?,
                    age_rating = ?,
                    language = ?,
                    country = ?,
                    director = ?,
                    movie_status = ?
                WHERE movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getDescription());

            if (movie.getReleaseYear() == null) {
                ps.setNull(3, Types.SMALLINT);
            } else {
                ps.setInt(3, movie.getReleaseYear());
            }

            ps.setInt(4, movie.getDurationMinutes());
            ps.setString(5, movie.getAgeRating());
            ps.setString(6, movie.getLanguage());
            ps.setString(7, movie.getCountry());
            ps.setString(8, movie.getDirector());
            ps.setString(9, movie.getMovieStatus().name());
            ps.setInt(10, movie.getMovieId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete Movie
    public boolean deleteMovie(int movieId) throws SQLException {

        if (movieId <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        String sql = """
                DELETE FROM movies
                WHERE movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private Movie mapResultSetToMovie(ResultSet resultSet) throws SQLException {

        Integer movieId = resultSet.getInt("movie_id");

        String title = resultSet.getString("title");
        String description = resultSet.getString("description");

        int releaseYearValue = resultSet.getInt("release_year");

        Integer releaseYear = resultSet.wasNull()
                ? null
                : releaseYearValue;

        int durationMinutes = resultSet.getInt("duration_minutes");

        String ageRating = resultSet.getString("age_rating");
        String language = resultSet.getString("language");
        String country = resultSet.getString("country");
        String director = resultSet.getString("director");

        MovieStatus movieStatus = MovieStatus.valueOf(
                resultSet.getString("movie_status")
        );

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        return new Movie(
                movieId,
                title,
                description,
                releaseYear,
                durationMinutes,
                ageRating,
                language,
                country,
                director,
                movieStatus,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
