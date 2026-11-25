package com.moviestreaming.application;

import com.moviestreaming.config.DatabaseConnection;
import com.moviestreaming.model.*;
import com.moviestreaming.model.enums.*;
import com.moviestreaming.service.*;
import com.moviestreaming.workflow.*;
import com.moviestreaming.report.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        try (Connection connection = DatabaseConnection.getConnection()) {

            System.out.println("========== MOVIE STREAMING PLATFORM ==========");

            runDemo(connection);

            System.out.println("\nDemo completed successfully.");

        } catch (SQLException exception) {

            System.out.println("Database error: " + exception.getMessage());
            exception.printStackTrace();

        } catch (RuntimeException exception) {

            System.out.println("Application error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }


    // Runs the application demo
    private static void runDemo(Connection connection) throws SQLException {

        // Services
        UserService users = new UserService(connection);
        MovieService movies = new MovieService(connection);
        GenreService genres = new GenreService(connection);
        MovieGenreService movieGenres = new MovieGenreService(connection);
        ReviewService reviews = new ReviewService(connection);
        WatchlistService watchlists = new WatchlistService(connection);
        WatchHistoryService history = new WatchHistoryService(connection);
        SubscriptionPlanService plans = new SubscriptionPlanService(connection);
        UserSubscriptionService subscriptions = new UserSubscriptionService(connection);


        // Workflows
        MovieGenreWorkflow genreWorkflow = new MovieGenreWorkflow(connection);
        ReviewWorkflow reviewWorkflow = new ReviewWorkflow(connection);
        WatchlistWorkflow watchlistWorkflow = new WatchlistWorkflow(connection);
        WatchHistoryWorkflow historyWorkflow = new WatchHistoryWorkflow(connection);
        UserSubscriptionWorkflow subscriptionWorkflow =
                new UserSubscriptionWorkflow(connection);


        // Reports
        UserReport userReport = new UserReport(connection);
        MovieReport movieReport = new MovieReport(connection);
        ReviewReport reviewReport = new ReviewReport(connection);
        WatchActivityReport watchReport = new WatchActivityReport(connection);
        SubscriptionReport subscriptionReport = new SubscriptionReport(connection);


        // Insert sample data
        int[] userIds = seedUsers(users);
        int[] movieIds = seedMovies(movies);
        int[] genreIds = seedGenres(genres);
        int[] planIds = seedPlans(plans);


        // Assign genres to movies
        assignGenres(genreWorkflow, movieIds, genreIds);


        // Insert reviews
        int[] reviewIds = seedReviews(reviewWorkflow, userIds, movieIds);


        // Insert watchlist and history
        seedWatchlist(watchlistWorkflow, userIds, movieIds);
        seedHistory(historyWorkflow, userIds, movieIds);


        // Insert subscriptions
        int[] subscriptionIds = seedSubscriptions(
                subscriptionWorkflow, userIds, planIds
        );


        // Test services
        testServices(
                users, movies, genres, movieGenres, reviews,
                watchlists, history, plans, subscriptions,
                userIds, movieIds, genreIds, planIds,
                reviewIds, subscriptionIds
        );


        // Display reports
        showReports(
                userReport, movieReport, reviewReport,
                watchReport, subscriptionReport, movieIds[0]
        );
    }


    // ==================== SAMPLE DATA ====================

    // Insert users
    private static int[] seedUsers(UserService service) throws SQLException {

        section("INSERT USERS");

        User admin = new User(
                "demo_admin",
                "demo_admin@example.com",
                "demo_hash_admin",
                "Omar",
                "Mattar"
        );

        admin.setUserRole(UserRole.ADMIN);


        User user1 = new User(
                "demo_ahmed",
                "demo_ahmed@example.com",
                "demo_hash_ahmed",
                "Ahmed",
                "Ali"
        );

        User user2 = new User(
                "demo_sara",
                "demo_sara@example.com",
                "demo_hash_sara",
                "Sara",
                "Mohamed"
        );

        User user3 = new User(
                "demo_mona",
                "demo_mona@example.com",
                "demo_hash_mona",
                "Mona",
                "Hassan"
        );


        int[] ids = {
                service.registerUser(admin),
                service.registerUser(user1),
                service.registerUser(user2),
                service.registerUser(user3)
        };


        System.out.println("Inserted " + ids.length + " users.");

        return ids;
    }


    // Insert movies
    private static int[] seedMovies(MovieService service) throws SQLException {

        section("INSERT MOVIES");

        Movie[] sampleMovies = {

                new Movie(
                        "Interstellar",
                        "A team travels through a wormhole to find a new home.",
                        2014,
                        169,
                        "PG-13",
                        "English",
                        "USA",
                        "Christopher Nolan"
                ),

                new Movie(
                        "Inception",
                        "A thief enters people's dreams to steal information.",
                        2010,
                        148,
                        "PG-13",
                        "English",
                        "USA",
                        "Christopher Nolan"
                ),

                new Movie(
                        "The Dark Knight",
                        "Batman faces the Joker in Gotham City.",
                        2008,
                        152,
                        "PG-13",
                        "English",
                        "USA",
                        "Christopher Nolan"
                ),

                new Movie(
                        "The Matrix",
                        "A programmer discovers the true nature of reality.",
                        1999,
                        136,
                        "R",
                        "English",
                        "USA",
                        "The Wachowskis"
                )
        };


        int[] ids = new int[sampleMovies.length];

        for (int i = 0; i < sampleMovies.length; i++) {

            ids[i] = service.addMovie(sampleMovies[i]);
        }


        System.out.println("Inserted " + ids.length + " movies.");

        return ids;
    }


    // Insert genres
    private static int[] seedGenres(GenreService service) throws SQLException {

        section("INSERT GENRES");

        Genre[] sampleGenres = {

                new Genre(
                        "Science Fiction",
                        "Science and future concepts."
                ),

                new Genre(
                        "Action",
                        "Action-packed stories."
                ),

                new Genre(
                        "Drama",
                        "Emotional and character-focused stories."
                ),

                new Genre(
                        "Thriller",
                        "Suspenseful stories."
                )
        };


        int[] ids = new int[sampleGenres.length];

        for (int i = 0; i < sampleGenres.length; i++) {

            ids[i] = service.addGenre(sampleGenres[i]);
        }


        System.out.println("Inserted " + ids.length + " genres.");

        return ids;
    }


    // Insert subscription plans
    private static int[] seedPlans(SubscriptionPlanService service)
            throws SQLException {

        section("INSERT SUBSCRIPTION PLANS");

        SubscriptionPlan[] samplePlans = {

                new SubscriptionPlan(
                        "Demo Basic",
                        new BigDecimal("49.99"),
                        30,
                        1,
                        "Basic plan for one device."
                ),

                new SubscriptionPlan(
                        "Demo Standard",
                        new BigDecimal("89.99"),
                        30,
                        2,
                        "Standard plan for two devices."
                ),

                new SubscriptionPlan(
                        "Demo Premium",
                        new BigDecimal("149.99"),
                        30,
                        4,
                        "Premium plan for four devices."
                )
        };


        int[] ids = new int[samplePlans.length];

        for (int i = 0; i < samplePlans.length; i++) {

            ids[i] = service.addSubscriptionPlan(samplePlans[i]);
        }


        System.out.println("Inserted " + ids.length + " plans.");

        return ids;
    }


    // Assign genres to movies
    private static void assignGenres(
            MovieGenreWorkflow workflow,
            int[] movieIds,
            int[] genreIds
    ) throws SQLException {

        section("ASSIGN GENRES");

        workflow.assignGenresToMovie(
                movieIds[0],
                List.of(genreIds[0], genreIds[2])
        );

        workflow.assignGenresToMovie(
                movieIds[1],
                List.of(genreIds[0], genreIds[3])
        );

        workflow.assignGenresToMovie(
                movieIds[2],
                List.of(genreIds[1], genreIds[2])
        );

        workflow.assignGenresToMovie(
                movieIds[3],
                List.of(genreIds[0], genreIds[1])
        );


        System.out.println("Movie genres assigned.");
    }


    // Insert reviews
    private static int[] seedReviews(
            ReviewWorkflow workflow,
            int[] userIds,
            int[] movieIds
    ) throws SQLException {

        section("INSERT REVIEWS");

        Review[] sampleReviews = {

                new Review(
                        userIds[0],
                        movieIds[0],
                        10,
                        "Excellent story, visuals and music."
                ),

                new Review(
                        userIds[1],
                        movieIds[0],
                        9,
                        "One of the best science fiction movies."
                ),

                new Review(
                        userIds[2],
                        movieIds[1],
                        8,
                        "A creative and interesting movie."
                ),

                new Review(
                        userIds[3],
                        movieIds[2],
                        10,
                        "Great acting and direction."
                )
        };


        int[] ids = new int[sampleReviews.length];

        for (int i = 0; i < sampleReviews.length; i++) {

            ids[i] = workflow.submitReview(sampleReviews[i]);
        }


        System.out.println("Inserted " + ids.length + " reviews.");

        return ids;
    }


    // Insert watchlist entries
    private static void seedWatchlist(
            WatchlistWorkflow workflow,
            int[] userIds,
            int[] movieIds
    ) throws SQLException {

        section("INSERT WATCHLIST");

        workflow.addMovieToWatchlist(userIds[0], movieIds[1]);
        workflow.addMovieToWatchlist(userIds[0], movieIds[2]);
        workflow.addMovieToWatchlist(userIds[1], movieIds[0]);
        workflow.addMovieToWatchlist(userIds[2], movieIds[3]);


        System.out.println("Watchlist entries inserted.");
    }


    // Insert watch history
    private static void seedHistory(
            WatchHistoryWorkflow workflow,
            int[] userIds,
            int[] movieIds
    ) throws SQLException {

        section("INSERT WATCH HISTORY");

        workflow.startWatching(userIds[0], movieIds[0]);

        workflow.updateWatchingProgress(
                userIds[0],
                movieIds[0],
                169 * 60,
                true
        );


        workflow.startWatching(userIds[1], movieIds[1]);

        workflow.updateWatchingProgress(
                userIds[1],
                movieIds[1],
                60 * 60,
                false
        );


        workflow.startWatching(userIds[2], movieIds[2]);

        workflow.updateWatchingProgress(
                userIds[2],
                movieIds[2],
                152 * 60,
                true
        );


        System.out.println("Watch history inserted and updated.");
    }


    // Insert subscriptions
    private static int[] seedSubscriptions(
            UserSubscriptionWorkflow workflow,
            int[] userIds,
            int[] planIds
    ) throws SQLException {

        section("INSERT SUBSCRIPTIONS");

        LocalDate start = LocalDate.now();

        UserSubscription[] sampleSubscriptions = {

                new UserSubscription(
                        userIds[0],
                        planIds[2],
                        start,
                        start.plusDays(30)
                ),

                new UserSubscription(
                        userIds[1],
                        planIds[1],
                        start,
                        start.plusDays(30)
                ),

                new UserSubscription(
                        userIds[2],
                        planIds[0],
                        start,
                        start.plusDays(30)
                )
        };


        int[] ids = new int[sampleSubscriptions.length];

        for (int i = 0; i < sampleSubscriptions.length; i++) {

            ids[i] = workflow.subscribeUser(sampleSubscriptions[i]);
        }


        System.out.println("Inserted " + ids.length + " subscriptions.");

        return ids;
    }


    // ==================== SERVICE TESTS ====================

    private static void testServices(
            UserService users,
            MovieService movies,
            GenreService genres,
            MovieGenreService movieGenres,
            ReviewService reviews,
            WatchlistService watchlists,
            WatchHistoryService history,
            SubscriptionPlanService plans,
            UserSubscriptionService subscriptions,
            int[] userIds,
            int[] movieIds,
            int[] genreIds,
            int[] planIds,
            int[] reviewIds,
            int[] subscriptionIds
    ) throws SQLException {


        // User service
        section("USER SERVICE");

        System.out.println("By ID: " + users.getUserById(userIds[0]));

        System.out.println("By username: " +
                users.getUserByUsername("demo_ahmed"));

        System.out.println("By email: " +
                users.getUserByEmail("demo_sara@example.com"));

        System.out.println("All users: " + users.getAllUsers().size());

        System.out.println("Active users: " +
                users.getActiveUsers().size());

        System.out.println("Admins: " +
                users.getAdministrators().size());


        User user = users.getUserById(userIds[1]);

        user.setFirstName("Ahmed Updated");

        System.out.println("Update user: " + users.updateUser(user));

        System.out.println("Suspend user: " +
                users.suspendUser(userIds[3]));

        System.out.println("Activate user: " +
                users.activateUser(userIds[3]));

        System.out.println("Promote user: " +
                users.promoteToAdmin(userIds[2]));

        System.out.println("Users by status: " +
                users.getUsersByStatus(UserStatus.ACTIVE).size());


        // Movie service
        section("MOVIE SERVICE");

        System.out.println("By ID: " +
                movies.getMovieById(movieIds[0]));

        System.out.println("Search: " +
                movies.searchMoviesByTitle("Dark"));

        System.out.println("All movies: " +
                movies.getAllMovies().size());

        System.out.println("Available: " +
                movies.getAvailableMovies().size());

        System.out.println("Sorted by title: " +
                movies.getMoviesSortedByTitle());

        System.out.println("Sorted by year: " +
                movies.getMoviesSortedByReleaseYear());

        System.out.println("Long movies: " +
                movies.getLongMovies());


        System.out.println("Make unavailable: " +
                movies.makeMovieUnavailable(movieIds[3]));

        System.out.println("Make available: " +
                movies.makeMovieAvailable(movieIds[3]));


        Movie movie = movies.getMovieById(movieIds[1]);

        movie.setDescription("Updated description from Main.");

        System.out.println("Update movie: " + movies.updateMovie(movie));


        // Genre and movie-genre services
        section("GENRE AND MOVIE-GENRE SERVICES");

        System.out.println("Genre by ID: " +
                genres.getGenreById(genreIds[0]));

        System.out.println("Genre by name: " +
                genres.getGenreByName("Action"));

        System.out.println("All genres: " + genres.getAllGenres());

        System.out.println("Sorted genres: " +
                genres.getGenresSortedByName());


        Genre genre = genres.getGenreById(genreIds[1]);

        genre.setDescription("Updated action description.");

        System.out.println("Update genre: " + genres.updateGenre(genre));


        System.out.println("Genres for movie: " +
                movieGenres.getGenresByMovie(movieIds[0]));

        System.out.println("Movies for genre: " +
                movieGenres.getMoviesByGenre(genreIds[0]));

        System.out.println("Relationship: " +
                movieGenres.getMovieGenre(movieIds[0], genreIds[0]));


        // Review service
        section("REVIEW SERVICE");

        System.out.println("Review by ID: " +
                reviews.getReviewById(reviewIds[0]));

        System.out.println("Reviews by movie: " +
                reviews.getReviewsByMovie(movieIds[0]));

        System.out.println("Reviews by user: " +
                reviews.getReviewsByUser(userIds[0]));

        System.out.println("All reviews: " +
                reviews.getAllReviews().size());

        System.out.println("Average rating: " +
                reviews.getAverageRatingByMovie(movieIds[0]));

        System.out.println("Sorted reviews: " +
                reviews.getReviewsSortedByRating(movieIds[0]));

        System.out.println("Positive reviews: " +
                reviews.getPositiveReviews(movieIds[0]));


        Review review = reviews.getReviewById(reviewIds[0]);

        review.updateReview(9, "Updated review from Main.");

        System.out.println("Update review: " + reviews.updateReview(review));


        // Watchlist service
        section("WATCHLIST SERVICE");

        System.out.println("By user: " +
                watchlists.getWatchlistByUser(userIds[0]));

        System.out.println("All entries: " +
                watchlists.getAllWatchlistEntries().size());

        System.out.println("Contains movie: " +
                watchlists.isMovieInWatchlist(userIds[0], movieIds[1]));

        System.out.println("Entry: " +
                watchlists.getWatchlistByUserAndMovie(
                        userIds[0],
                        movieIds[1]
                ));

        System.out.println("Remove entry: " +
                watchlists.removeFromWatchlist(userIds[0], movieIds[2]));

        System.out.println("Add entry again: " +
                watchlists.addToWatchlist(userIds[0], movieIds[2]));


        // Watch history service
        section("WATCH HISTORY SERVICE");

        System.out.println("By user: " +
                history.getWatchHistoryByUser(userIds[0]));

        System.out.println("All records: " +
                history.getAllWatchHistory().size());

        System.out.println("By user and movie: " +
                history.getWatchHistoryByUserAndMovie(
                        userIds[0],
                        movieIds[0]
                ));

        System.out.println("Completed: " +
                history.getCompletedMovies(userIds[0]));

        System.out.println("Incomplete: " +
                history.getIncompleteMovies(userIds[1]));

        System.out.println("Sorted by progress: " +
                history.getWatchHistorySortedByProgress(userIds[0]));

        System.out.println("Update progress: " +
                history.updateWatchProgress(
                        userIds[1],
                        movieIds[1],
                        90 * 60,
                        true
                ));


        // Subscription services
        section("SUBSCRIPTION SERVICES");

        System.out.println("Plan by ID: " +
                plans.getPlanById(planIds[0]));

        System.out.println("Plan by name: " +
                plans.getPlanByName("Demo Basic"));

        System.out.println("All plans: " + plans.getAllPlans());

        System.out.println("Active plans: " + plans.getActivePlans());

        System.out.println("Sorted plans: " +
                plans.getPlansSortedByPrice());


        SubscriptionPlan plan = plans.getPlanById(planIds[0]);

        plan.updatePrice(new BigDecimal("54.99"));

        System.out.println("Update plan: " +
                plans.updateSubscriptionPlan(plan));


        System.out.println("Subscription by ID: " +
                subscriptions.getSubscriptionById(subscriptionIds[0]));

        System.out.println("By user: " +
                subscriptions.getSubscriptionsByUser(userIds[0]));

        System.out.println("By plan: " +
                subscriptions.getSubscriptionsByPlan(planIds[0]));

        System.out.println("Active subscription: " +
                subscriptions.getActiveSubscriptionByUser(userIds[0]));

        System.out.println("All subscriptions: " +
                subscriptions.getAllSubscriptions().size());

        System.out.println("Expired subscriptions: " +
                subscriptions.getExpiredSubscriptions().size());

        System.out.println("Cancel subscription: " +
                subscriptions.cancelSubscription(subscriptionIds[2]));
    }


    // ==================== REPORTS ====================

    // Display application reports
    private static void showReports(
            UserReport users,
            MovieReport movies,
            ReviewReport reviews,
            WatchActivityReport watch,
            SubscriptionReport subscriptions,
            int movieId
    ) throws SQLException {


        // User report
        section("USER REPORT");

        System.out.println("Total: " + users.getTotalUsers());

        System.out.println("Active: " + users.getActiveUsersCount());

        System.out.println("Suspended: " + users.getSuspendedUsersCount());

        System.out.println("Closed: " + users.getClosedUsersCount());

        System.out.println("Administrators: " +
                users.getAdministratorsCount());

        System.out.println("Sorted users: " +
                users.getUsersSortedByUsername());


        // Movie report
        section("MOVIE REPORT");

        System.out.println("Total: " + movies.getTotalMovies());

        System.out.println("Available: " + movies.getAvailableMoviesCount());

        System.out.println("Unavailable: " +
                movies.getUnavailableMoviesCount());

        System.out.println("Average duration: " +
                movies.getAverageMovieDuration());

        System.out.println("Sorted movies: " +
                movies.getMoviesSortedByTitle());

        System.out.println("Long movies: " +
                movies.getLongMovies(120));


        // Review report
        section("REVIEW REPORT");

        System.out.println("Total: " + reviews.getTotalReviews());

        System.out.println("Average for movie: " +
                reviews.getAverageRatingByMovie(movieId));

        System.out.println("Positive: " +
                reviews.getPositiveReviewsCount());

        System.out.println("Negative: " +
                reviews.getNegativeReviewsCount());

        System.out.println("Averages by movie: " +
                reviews.getAverageRatingsByMovie());

        System.out.println("Sorted reviews: " +
                reviews.getReviewsSortedByRating());


        // Watch activity report
        section("WATCH ACTIVITY REPORT");

        System.out.println("Total records: " +
                watch.getTotalWatchRecords());

        System.out.println("Completed: " +
                watch.getCompletedMoviesCount());

        System.out.println("Incomplete: " +
                watch.getIncompleteMoviesCount());

        System.out.println("Completion rate: " +
                watch.getCompletionRate() + "%");

        System.out.println("Watched seconds by user: " +
                watch.getTotalWatchSecondsByUser());

        System.out.println("Records by user: " +
                watch.getWatchRecordsCountByUser());

        System.out.println("Recently watched: " +
                watch.getMostRecentlyWatched());


        // Subscription report
        section("SUBSCRIPTION REPORT");

        System.out.println("Total: " +
                subscriptions.getTotalSubscriptions());

        System.out.println("Active: " +
                subscriptions.getActiveSubscriptionsCount());

        System.out.println("Expired: " +
                subscriptions.getExpiredSubscriptionsCount());

        System.out.println("Cancelled: " +
                subscriptions.getCancelledSubscriptionsCount());

        System.out.println("By plan: " +
                subscriptions.getSubscriptionsCountByPlan());

        System.out.println("Active by plan: " +
                subscriptions.getActiveSubscriptionsCountByPlan());

        System.out.println("Sorted by date: " +
                subscriptions.getSubscriptionsSortedByDate());
    }


    // Print section title
    private static void section(String title) {

        System.out.println("\n========== " + title + " ==========");
    }
}