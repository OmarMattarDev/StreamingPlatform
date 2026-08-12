import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) {

        StreamingPlatform platform = new StreamingPlatform();

        // =========================
        // Create Movies
        // =========================

        Movie movie1 = new Movie(
                1,
                "Inception",
                Genre.SCI_FI,
                2010,
                148,
                true
        );

        Movie movie2 = new Movie(
                2,
                "The Dark Knight",
                Genre.ACTION,
                2008,
                152,
                true
        );

        Movie movie3 = new Movie(
                3,
                "Interstellar",
                Genre.SCI_FI,
                2014,
                169,
                false
        );

        Movie movie4 = new Movie(
                4,
                "The Godfather",
                Genre.DRAMA,
                1972,
                175,
                true
        );

        // =========================
        // Add Ratings
        // =========================

        movie1.addRating(5);
        movie1.addRating(4);
        movie1.addRating(5);

        movie2.addRating(5);
        movie2.addRating(5);
        movie2.addRating(4);

        movie3.addRating(5);
        movie3.addRating(4);

        movie4.addRating(5);
        movie4.addRating(5);
        movie4.addRating(5);

        // =========================
        // Add Movies to Platform
        // =========================

        platform.addMovie(movie1);
        platform.addMovie(movie2);
        platform.addMovie(movie3);
        platform.addMovie(movie4);

        // =========================
        // Create Users
        // =========================

        User user1 = new User(
                1,
                "Omar",
                21,
                SubscriptionType.PREMIUM,
                500
        );

        User user2 = new User(
                2,
                "Ali",
                22,
                SubscriptionType.BASIC,
                250
        );

        User user3 = new User(
                3,
                "Ahmed",
                20,
                SubscriptionType.PREMIUM,
                400
        );

        // =========================
        // Favorite Genres
        // =========================

        user1.addFavoriteGenre(Genre.SCI_FI);
        user1.addFavoriteGenre(Genre.ACTION);

        user2.addFavoriteGenre(Genre.DRAMA);

        user3.addFavoriteGenre(Genre.SCI_FI);

        // =========================
        // Add Users
        // =========================

        platform.addUser(user1);
        platform.addUser(user2);
        platform.addUser(user3);

        // =========================
        // Watch Movies
        // =========================

        platform.watchMovie(1, 1);
        platform.watchMovie(1, 2);

        platform.watchMovie(2, 4);

        platform.watchMovie(3, 1);

        // =========================
        // Add Reviews
        // =========================

        Review review1 = new Review(
                user1,
                movie1,
                5,
                "Amazing movie!"
        );

        Review review2 = new Review(
                user2,
                movie4,
                5,
                "A timeless classic."
        );

        Review review3 = new Review(
                user3,
                movie2,
                5,
                "One of the best movies ever."
        );

        platform.addReview(review1);
        platform.addReview(review2);
        platform.addReview(review3);

        // =========================
        // Display Movies
        // =========================

        System.out.println("========== ALL MOVIES ==========");
        platform.getMovies().values()
                .forEach(System.out::println);

        // =========================
        // Available Movies
        // =========================

        System.out.println("========== AVAILABLE MOVIES ==========");
        platform.getAvailableMovies()
                .forEach(movie ->
                        System.out.println(movie.getTitle())
                );

        // =========================
        // Top Rated Movies
        // =========================

        System.out.println("========== TOP 3 RATED MOVIES ==========");

        platform.getTop3RatedMovies()
                .forEach(movie ->
                        System.out.println(
                                movie.getTitle()
                                        + " -> "
                                        + movie.averageRating()
                        )
                );

        // =========================
        // Movies By Genre
        // =========================

        System.out.println("========== SCI-FI MOVIES ==========");

        platform.getMoviesByGenre(Genre.SCI_FI)
                .forEach(movie ->
                        System.out.println(movie.getTitle())
                );

        // =========================
        // Movie Statistics
        // =========================

        System.out.println("========== STATISTICS ==========");

        System.out.println(
                "Average Movie Rating: "
                        + platform.getAverageMovieRating()
        );

        System.out.println(
                "Average Movie Duration: "
                        + platform.getAverageMovieDuration()
        );

        System.out.println(
                "Total Movie Duration: "
                        + platform.getTotalMovieDuration()
        );

        System.out.println(
                "Available Movies: "
                        + platform.countAvailableMovies()
        );

        System.out.println(
                "Unavailable Movies: "
                        + platform.countUnavailableMovies()
        );

        // =========================
        // Users
        // =========================

        System.out.println("========== USERS ==========");

        platform.getUsers().values()
                .forEach(user ->
                        System.out.println(
                                user.getName()
                                        + " -> "
                                        + user.getSubscriptionType()
                        )
                );

        // =========================
        // Watched Movies
        // =========================

        System.out.println("========== ALL WATCHED MOVIES ==========");

        platform.getAllWatchedMovies()
                .forEach(movie ->
                        System.out.println(movie.getTitle())
                );

        // =========================
        // Grouping
        // =========================

        System.out.println("========== MOVIES BY GENRE ==========");

        platform.groupMoviesByGenre()
                .forEach((genre, movies) ->
                        System.out.println(
                                genre + " -> "
                                        + movies.stream()
                                        .map(Movie::getTitle)
                                        .toList()
                        )
                );

        // =========================
        // Most Popular Genre
        // =========================

        System.out.println("========== MOST POPULAR GENRE ==========");

        System.out.println(
                platform.getMostPopularGenre()
                        .orElse(null)
        );

        // =========================
        // Top Reviewer
        // =========================

        System.out.println("========== TOP REVIEWER ==========");

        platform.getTopReviewer()
                .ifPresent(user ->
                        System.out.println(user.getName())
                );
    }
}