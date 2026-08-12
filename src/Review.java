public class Review {

    private User user;

    private Movie movie;

    private int rating;

    private String comment;

    public Review
            (
                    User user,
                    Movie movie,
                    int rating,
                    String comment
            )
    {
        setUser(user);
        setMovie(movie);
        setRating(rating);
        setComment(comment);
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    private void setUser(User user) {
        if(user==null){
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }

    private void setMovie(Movie movie) {
        if(movie==null){
            throw new IllegalArgumentException("Movie cannot be null");
        }
        this.movie = movie;
    }

    private void setRating(int rating) {
        if(rating<1||rating>5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    private void setComment(String comment) {
        if(comment==null||comment.isBlank()){
            throw new IllegalArgumentException("Comment cannot be null or blank");
        }
        this.comment = comment;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\nUser : ").append(user.getName())
                .append("\nMovie : ").append(movie.getTitle())
                .append("\nRating : ").append(rating)
                .append("\nComment : ").append(comment)
                .append("\n")
                .toString();
    }
}
