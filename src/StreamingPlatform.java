import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class StreamingPlatform {

    private Map<Integer, Movie> movies;

    private Map<Integer, User> users;

    private List<Review> reviews;

    public StreamingPlatform(){
        movies=new HashMap<>();
        users=new HashMap<>();
        reviews=new ArrayList<>();
    }

    public Map<Integer, Movie> getMovies() {
        return new HashMap<>(movies);
    }

    public Map<Integer, User> getUsers() {
        return new HashMap<>(users);
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
    }

    public boolean addMovie(Movie movie){
        if(movie==null){
            throw new IllegalArgumentException("Movie cannot be null");
        }
        return movies.putIfAbsent(movie.getId(),movie)==null;
    }

    public boolean removeMovie(int id){
        if(id<=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        return movies.remove(id)!=null;
    }

    public Movie findMovie(int id){
        if(id<=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        return movies.get(id);
    }

    public void displayMovies(){
        movies
                .values()
                .forEach(
                        System.out::println
                );
    }

    public int getMovieCount(){
        return movies.size();
    }

    public boolean addUser(User user){
        if(user==null){
            throw new IllegalArgumentException("User cannot be null");
        }
        return users.putIfAbsent(user.getId(),user)==null;
    }

    public boolean removeUser(int id){
        if(id<=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        return users.remove(id)!=null;
    }

    public User findUser(int id){
        if(id<=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        return users.get(id);
    }

    public void displayUsers(){
        users
                .values()
                .forEach(
                        System.out::println
                );
    }

    public int getUserCount(){
        return users.size();
    }

    public boolean addReview(Review review){
        if(review==null){
            throw new IllegalArgumentException("Review cannot be null");
        }
        if(!movies.containsKey(review.getMovie().getId())){
            throw new IllegalArgumentException("Movie not found");
        }
        if(!users.containsKey(review.getUser().getId())){
            throw new IllegalArgumentException("User not found");
        }
        reviews.add(review);
        review.getMovie().addRating(review.getRating());
        return true;
    }

    public void displayReviews(){
        reviews
                .forEach(
                        System.out::println
                );
    }

    public int getReviewCount(){
        return reviews.size();
    }

    public boolean watchMovie(
            int userId,
            int movieId
    )
    {
        User user = findUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Movie movie = findMovie(movieId);
        if (movie == null) {
            throw new IllegalArgumentException("Movie not found");
        }

        user.watchMovie(movie);
        return true;
    }

    public List<Movie> searchMovies(String keyword){
        return movies.values()
                .stream()
                .filter(movie -> movie.matches(keyword))
                .toList();
    }

    public List<Movie> getAvailableMovies(){
        return
                movies
                        .values()
                        .stream()
                        .filter(
                                Movie::isAvailable
                        )
                        .toList();
    }

    public List<Movie> getUnavailableMovies(){
        return
                movies
                        .values()
                        .stream()
                        .filter(
                                x->!x.isAvailable()
                        )
                        .toList();
    }

    public List<Movie> getMoviesByGenre(Genre genre){
        if(genre==null){
            throw new IllegalArgumentException("Genre cannot be null");
        }
        return
                movies
                        .values()
                        .stream()
                        .filter(
                                x->x.getGenre().equals(genre)
                        )
                        .toList();
    }

    public List<Movie> getMoviesReleasedAfter(int year){
        if(year<1888){
            throw new IllegalArgumentException("Year must be greater than 1887");
        }
        return
                movies
                        .values()
                        .stream()
                        .filter(
                                x->x.getReleaseYear()>year
                        )
                        .toList();
    }

    public List<Movie> getLongMovies(int minDuration){
        if(minDuration<=0){
            throw new IllegalArgumentException("minDuration must be greater than 0");
        }
        return
                movies
                        .values()
                        .stream()
                        .filter(
                                x->x.getDuration()>minDuration
                        )
                        .toList();
    }

    public List<Movie> getHighRatedMovies(double minRating){
        if(minRating<1||minRating>5){
            throw new IllegalArgumentException("Rating Must Be Between 1 And 5");
        }
        return
                movies
                        .values()
                        .stream()
                        .filter(
                                x->x.averageRating()>=minRating
                        )
                        .toList();
    }

    public List<User> getUsersWithFavoriteGenre(Genre genre){
        if(genre==null){
            throw new IllegalArgumentException("Genre cannot be null");
        }
        return
                users
                        .values()
                        .stream()
                        .filter(
                                x->x.hasFavoriteGenre(genre)
                        )
                        .toList();
    }

    public List<User> getUsersBySubscription(SubscriptionType type){
        if(type==null){
            throw new IllegalArgumentException("Type cannot be null");
        }
        return
                users
                        .values()
                        .stream()
                        .filter(
                                x->x.getSubscriptionType().equals(type)
                        )
                        .toList();
    }

    public List<User> getUsersWithWatchHistory(){
        return
                users
                        .values()
                        .stream()
                        .filter(
                                User::hasWatchHistory
                        )
                        .toList();
    }

    public List<Review> getReviewsByRating(int rating){
        if(rating<1||rating>5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return
                reviews
                        .stream()
                        .filter(
                                x->x.getRating()==rating
                        )
                        .toList();
    }

    public List<String> getMovieTitles(){
        return movies
                .values()
                .stream()
                .map(
                        Movie::getTitle
                )
                .toList();
    }

    public List<String> getUserNames(){
        return users
                .values()
                .stream()
                .map(
                        User::getName
                )
                .toList();
    }

    public List<Double> getMovieRatings(){
        return movies
                .values()
                .stream()
                .map(
                        Movie::averageRating
                )
                .toList();
    }

    public List<Integer> getMovieDurations(){
        return movies
                .values()
                .stream()
                .map(
                        Movie::getDuration
                )
                .toList();
    }

    public Set<Genre> getAllGenres(){
        return movies
                .values()
                .stream()
                .map(
                        Movie::getGenre
                )
                .collect(
                        Collectors
                                .toSet()
                );
    }

    public List<Movie> sortMoviesByTitle(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::getTitle
                                )
                )
                .toList();
    }

    public List<Movie> sortMoviesByRating(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::averageRating
                                )
                )
                .toList();
    }

    public List<Movie> sortMoviesByRatingDescending(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::averageRating
                                )
                                .reversed()
                )
                .toList();
    }

    public List<Movie> sortMoviesByReleaseYear(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::getReleaseYear
                                )
                )
                .toList();
    }

    public List<Movie> sortMoviesByGenreThenTitle(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::getGenre
                                )
                                .thenComparing(
                                        Movie::getTitle
                                )
                )
                .toList();
    }

    public double getAverageMovieRating(){
        return movies
                .values()
                .stream()
                .mapToDouble(
                        Movie::averageRating
                )
                .average()
                .orElse(0);
    }

    public double getAverageMovieDuration(){
        return movies
                .values()
                .stream()
                .mapToInt(
                        Movie::getDuration
                )
                .average()
                .orElse(0);
    }

    public int getTotalMovieDuration(){
        return movies
                .values()
                .stream()
                .mapToInt(
                        Movie::getDuration
                )
                .sum();
    }

    public long countAvailableMovies(){
        return movies
                .values()
                .stream()
                .filter(
                        Movie::isAvailable
                )
                .count();
    }

    public long countUnavailableMovies(){
        return movies
                .values()
                .stream()
                .filter(
                        movie -> ! movie.isAvailable()
                )
                .count();
    }

    public Map<Genre, Long> countMoviesByGenre(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Movie::getGenre,
                                        Collectors
                                                .counting()
                                )
                );
    }

    public Optional<Movie> getHighestRatedMovie(){
        return movies
                .values()
                .stream()
                .max(
                        Comparator
                                .comparing(
                                        Movie::averageRating
                                )
                );
    }

    public Optional<Movie> getLowestRatedMovie(){
        return movies
                .values()
                .stream()
                .min(
                        Comparator
                                .comparing(
                                        Movie::averageRating
                                )
                );
    }

    public Optional<Movie> getLongestMovie(){
        return movies
                .values()
                .stream()
                .max(
                        Comparator
                                .comparing(
                                        Movie::getDuration
                                )
                );
    }

    public Optional<Movie> getShortestMovie(){
        return movies
                .values()
                .stream()
                .min(
                        Comparator
                                .comparing(
                                        Movie::getDuration
                                )
                );
    }

    public Optional<User> getMostActiveUser(){
        return users
                .values()
                .stream()
                .max(
                        Comparator
                                .comparing(
                                        User::getWatchCount
                                )
                );
    }

    public Map<Genre,List<Movie>> groupMoviesByGenre(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Movie::getGenre
                                )
                );
    }

    public Map<SubscriptionType,List<User>> groupUsersBySubscription(){
        return users
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        User::getSubscriptionType
                                )
                );
    }

    public Map<Integer,List<Review>> groupReviewsByRating(){
        return reviews
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Review::getRating
                                )
                );
    }

    public Map<Boolean,List<Movie>> partitionMoviesByAvailability(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .partitioningBy(
                                        Movie::isAvailable
                                )
                );
    }

    public Map<Boolean,List<Movie>> partitionMoviesByHighRating(double rating){
        if(rating<1||rating>5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .partitioningBy(
                                        x->x.averageRating()>rating
                                )
                );
    }

    public Map<SubscriptionType,Long> countUsersPerSubscription(){
        return users
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        User::getSubscriptionType,
                                        Collectors
                                                .counting()
                                )
                );
    }

    public Map<Genre, Double> averageRatingPerGenre(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Movie::getGenre,
                                        Collectors
                                                .averagingDouble(
                                                        Movie::averageRating
                                                )
                                )
                );
    }

    public Map<Genre,Double> averageDurationPerGenre(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Movie::getGenre,
                                        Collectors
                                                .averagingDouble(
                                                        Movie::getDuration
                                                )
                                )
                );
    }

    public Map<Genre,Integer> totalDurationPerGenre(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Movie::getGenre,
                                        Collectors
                                                .summingInt(
                                                        Movie::getDuration
                                                )
                                )
                );
    }

    public String getAllMovieTitles(){
        return movies
                .values()
                .stream()
                .map(
                        Movie::getTitle
                )
                .collect(
                        Collectors
                                .joining(", ")
                );
    }

    public String getAllUserNames(){
        return users
                .values()
                .stream()
                .map(
                        User::getName
                )
                .collect(
                        Collectors
                                .joining(", ")
                );
    }

    public List<Integer> getReleaseYears(){
        return movies
                .values()
                .stream()
                .map(
                        Movie::getReleaseYear
                )
                .distinct()
                .toList();
    }

    public List<Movie> getAllWatchedMovies(){
        return users
                .values()
                .stream()
                .map(
                        User::getWatchHistory
                )
                .flatMap(
                        List::stream
                )
                .distinct()
                .toList();
    }

    public int getTotalRatings(){
        return movies
                .values()
                .stream()
                .flatMap(
                        movie -> movie.getRatings().stream()
                )
                .reduce(
                        0,
                        Integer::sum
                );
    }

    public IntSummaryStatistics getMovieDurationStatistics(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .summarizingInt(
                                        Movie::getDuration
                                )
                );
    }

    public DoubleSummaryStatistics getMovieRatingStatistics(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .summarizingDouble(
                                        Movie::averageRating
                                )
                );
    }

    public Map<Integer,String> movieIdToTitle(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .toMap(
                                        Movie::getId,
                                        Movie::getTitle
                                )
                );
    }

    public Map<String,Movie> movieTitleToMovie(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .toMap(
                                        Movie::getTitle,
                                        Function.identity(),
                                        (m1,m2)->m1
                                )
                );
    }

    public List<Movie> getTop3RatedMovies(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::averageRating
                                )
                                .reversed()
                )
                .limit(3)
                .toList();
    }

    public List<Movie> getNewestMovies(int n){
        if(n<=0){
            throw new IllegalArgumentException("n must be greater than 0");
        }
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::getReleaseYear
                                )
                                .reversed()
                )
                .limit(n)
                .toList();
    }

    public Optional<Movie> getOldestMovie(){
        return movies
                .values()
                .stream()
                .min(
                        Comparator
                                .comparing(
                                        Movie::getReleaseYear
                                )
                );
    }

    public Optional<Movie> getNewestMovie(){
        return movies
                .values()
                .stream()
                .max(
                        Comparator
                                .comparing(
                                        Movie::getReleaseYear
                                )
                );
    }

    public List<Movie> sortMoviesByRatingsCount(){
        return movies
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::getRatingCount
                                )
                                .reversed()
                )
                .toList();
    }

    public Optional<Genre> getMostPopularGenre(){
        return movies
                .values()
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Movie::getGenre,
                                        Collectors
                                                .counting()
                                )
                )
                .entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey);
    }

    public Optional<User> getTopReviewer(){
        return reviews
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Review::getUser,
                                        Collectors
                                                .counting()
                                )
                )
                .entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey);
    }
}
