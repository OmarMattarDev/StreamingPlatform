
package com.moviestreaming.dao;

import com.moviestreaming.model.MovieGenre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieGenreDAO {

    private final Connection connection;

    public MovieGenreDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Genre To Movie
    public boolean addGenreToMovie(MovieGenre movieGenre) throws SQLException {

        if (movieGenre == null) {
            throw new IllegalArgumentException("MovieGenre cannot be null");
        }

        if (movieGenre.getMovieId() <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        if (movieGenre.getGenreId() <= 0) {
            throw new IllegalArgumentException("Genre ID must be positive");
        }

        String sql = """
                INSERT INTO movie_genres
                (movie_id, genre_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieGenre.getMovieId());
            ps.setInt(2, movieGenre.getGenreId());

            return ps.executeUpdate() > 0;
        }
    }

    // Get MovieGenre By IDs
    public MovieGenre getMovieGenre(int movieId, int genreId) throws SQLException {

        if (movieId <= 0 || genreId <= 0) {
            throw new IllegalArgumentException("Movie ID and Genre ID must be positive");
        }

        String sql = """
                SELECT *
                FROM movie_genres
                WHERE movie_id = ? AND genre_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, genreId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToMovieGenre(resultSet);
                }

                return null;
            }
        }
    }

    // Get Genres By Movie ID
    public List<MovieGenre> getGenresByMovieId(int movieId) throws SQLException {

        if (movieId <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        String sql = """
                SELECT *
                FROM movie_genres
                WHERE movie_id = ?
                """;

        List<MovieGenre> movieGenres = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    movieGenres.add(mapResultSetToMovieGenre(resultSet));
                }
            }
        }

        return movieGenres;
    }

    // Get Movies By Genre ID
    public List<MovieGenre> getMoviesByGenreId(int genreId) throws SQLException {

        if (genreId <= 0) {
            throw new IllegalArgumentException("Genre ID must be positive");
        }

        String sql = """
                SELECT *
                FROM movie_genres
                WHERE genre_id = ?
                """;

        List<MovieGenre> movieGenres = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, genreId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    movieGenres.add(mapResultSetToMovieGenre(resultSet));
                }
            }
        }

        return movieGenres;
    }

    // Remove Genre From Movie
    public boolean removeGenreFromMovie(int movieId, int genreId)
            throws SQLException {

        if (movieId <= 0 || genreId <= 0) {
            throw new IllegalArgumentException("Movie ID and Genre ID must be positive");
        }

        String sql = """
                DELETE FROM movie_genres
                WHERE movie_id = ? AND genre_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, genreId);

            return ps.executeUpdate() > 0;
        }
    }

    // Remove All Genres From Movie
    public boolean removeAllGenresFromMovie(int movieId) throws SQLException {

        if (movieId <= 0) {
            throw new IllegalArgumentException("Movie ID must be positive");
        }

        String sql = """
                DELETE FROM movie_genres
                WHERE movie_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private MovieGenre mapResultSetToMovieGenre(ResultSet resultSet)
            throws SQLException {

        Integer movieId = resultSet.getInt("movie_id");
        Integer genreId = resultSet.getInt("genre_id");

        return new MovieGenre(
                movieId,
                genreId
        );
    }
}
