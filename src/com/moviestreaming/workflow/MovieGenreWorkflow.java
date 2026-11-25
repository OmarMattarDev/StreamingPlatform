
package com.moviestreaming.workflow;

import com.moviestreaming.dao.GenreDAO;
import com.moviestreaming.dao.MovieDAO;
import com.moviestreaming.dao.MovieGenreDAO;
import com.moviestreaming.model.Genre;
import com.moviestreaming.model.Movie;
import com.moviestreaming.model.MovieGenre;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieGenreWorkflow {

    private final Connection connection;
    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;
    private final MovieGenreDAO movieGenreDAO;

    public MovieGenreWorkflow(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.connection = connection;
        this.movieDAO = new MovieDAO(connection);
        this.genreDAO = new GenreDAO(connection);
        this.movieGenreDAO = new MovieGenreDAO(connection);
    }

    public int assignGenresToMovie(
            Integer movieId,
            List<Integer> genreIds
    ) throws SQLException {

        validateId(movieId, "Movie ID");

        if (genreIds == null || genreIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Genre IDs cannot be null or empty."
            );
        }

        validateGenreIds(genreIds);

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    Movie movie = movieDAO.getMovieById(movieId);

                    if (movie == null) {
                        throw new IllegalArgumentException(
                                "Movie does not exist."
                        );
                    }

                    for (Integer genreId : genreIds) {

                        Genre genre = genreDAO.getGenreById(genreId);

                        if (genre == null) {
                            throw new IllegalArgumentException(
                                    "Genre does not exist: " + genreId
                            );
                        }

                        MovieGenre existingRelation =
                                movieGenreDAO.getMovieGenre(
                                        movieId,
                                        genreId
                                );

                        if (existingRelation != null) {
                            throw new IllegalArgumentException(
                                    "Genre is already assigned: " + genreId
                            );
                        }
                    }

                    int assignedCount = 0;

                    for (Integer genreId : genreIds) {

                        MovieGenre relation =
                                new MovieGenre(movieId, genreId);

                        if (!movieGenreDAO.addGenreToMovie(relation)) {
                            throw new SQLException(
                                    "Failed to assign genre: " + genreId
                            );
                        }

                        assignedCount++;
                    }

                    return assignedCount;
                }
        );
    }

    public boolean removeGenreFromMovie(
            Integer movieId,
            Integer genreId
    ) throws SQLException {

        validateId(movieId, "Movie ID");
        validateId(genreId, "Genre ID");

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    Movie movie = movieDAO.getMovieById(movieId);

                    if (movie == null) {
                        throw new IllegalArgumentException(
                                "Movie does not exist."
                        );
                    }

                    MovieGenre relation =
                            movieGenreDAO.getMovieGenre(movieId, genreId);

                    if (relation == null) {
                        return false;
                    }

                    return movieGenreDAO.removeGenreFromMovie(
                            movieId,
                            genreId
                    );
                }
        );
    }

    private void validateGenreIds(List<Integer> genreIds) {

        Set<Integer> uniqueGenreIds = new HashSet<>();

        for (Integer genreId : genreIds) {

            validateId(genreId, "Genre ID");

            if (!uniqueGenreIds.add(genreId)) {
                throw new IllegalArgumentException(
                        "Duplicate genre ID: " + genreId
                );
            }
        }
    }

    private void validateId(Integer id, String fieldName) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}
