
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

public class MovieCatalogWorkflow {

    private final Connection connection;
    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;
    private final MovieGenreDAO movieGenreDAO;

    public MovieCatalogWorkflow(Connection connection) {
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

    public Integer registerMovieWithGenres(
            Movie movie,
            List<Integer> genreIds
    ) throws SQLException {

        if (movie == null) {
            throw new IllegalArgumentException(
                    "Movie cannot be null."
            );
        }

        if (genreIds == null) {
            throw new IllegalArgumentException(
                    "Genre IDs cannot be null."
            );
        }

        validateGenreIds(genreIds);

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    for (Integer genreId : genreIds) {
                        Genre genre = genreDAO.getGenreById(genreId);

                        if (genre == null) {
                            throw new IllegalArgumentException(
                                    "Genre does not exist: " + genreId
                            );
                        }
                    }

                    int movieId = movieDAO.addMovie(movie);

                    if (movieId <= 0) {
                        throw new SQLException(
                                "Failed to register movie."
                        );
                    }

                    for (Integer genreId : genreIds) {

                        MovieGenre movieGenre =
                                new MovieGenre(movieId, genreId);

                        boolean added =
                                movieGenreDAO.addGenreToMovie(movieGenre);

                        if (!added) {
                            throw new SQLException(
                                    "Failed to assign genre: " + genreId
                            );
                        }
                    }

                    return movieId;
                }
        );
    }

    private void validateGenreIds(List<Integer> genreIds) {

        Set<Integer> uniqueGenreIds = new HashSet<>();

        for (Integer genreId : genreIds) {

            if (genreId == null || genreId <= 0) {
                throw new IllegalArgumentException(
                        "Genre ID must be greater than zero."
                );
            }

            if (!uniqueGenreIds.add(genreId)) {
                throw new IllegalArgumentException(
                        "Duplicate genre ID: " + genreId
                );
            }
        }
    }
}
