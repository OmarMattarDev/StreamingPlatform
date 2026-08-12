import java.util.*;

public class Movie
        implements
        Rateable,Searchable
{

    private int id,releaseYear,duration;

    private String title;

    private Genre genre;

    private List<Integer> ratings;

    private boolean available;

    public Movie(
            int id,
            String title,
            Genre genre,
            int releaseYear,
            int duration,
            boolean available
    )
    {
        setId(id);
        setTitle(title);
        setGenre(genre);
        setReleaseYear(releaseYear);
        setDuration(duration);
        setAvailable(available);
        this.ratings = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isAvailable() {
        return available;
    }

    public List<Integer> getRatings() {
        return new ArrayList<>(ratings);
    }

    private void setId(int id) {
        if(id<=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        this.id = id;
    }

    private void setTitle(String title) {
        if(title==null||title.isBlank()){
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        this.title = title;
    }

    private void setGenre(Genre genre) {
        if(genre==null){
            throw new IllegalArgumentException("Genre cannot be null");
        }
        this.genre = genre;
    }

    private void setReleaseYear(int releaseYear) {
        if(releaseYear<1888||releaseYear>2030){
            throw new IllegalArgumentException("Release Year must be between 1888 and 2030");
        }
        this.releaseYear = releaseYear;
    }

    private void setDuration(int duration) {
        if(duration<=0){
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        this.duration = duration;
    }

    private void setAvailable(boolean available) {
        this.available = available;
    }

    public void changeAvailability(boolean available){
        if(this.available==available){
            throw new IllegalArgumentException("Movie is already "+available);
        }
        setAvailable(available);
    }

    public void addRating(int rating){
        if(rating<1||rating>5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.ratings.add(rating);
    }

    @Override
    public double averageRating(){
        return ratings
                .stream()
                .mapToInt(
                        Integer::intValue
                )
                .average()
                .orElse(0);
    }

    public int getRatingCount(){
        return ratings.size();
    }

    @Override
    public boolean matches(String keyword) {
        if(keyword==null||keyword.isBlank()){
            throw new IllegalArgumentException("Keyword cannot be null or blank");
        }
        keyword=keyword.trim().toLowerCase(Locale.ROOT);
        return title.toLowerCase(Locale.ROOT).contains(keyword);
    }

    public boolean hasRatings() {
        return !ratings.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\nId : ").append(id)
                .append("\nTitle : ").append(title)
                .append("\nGenre : ").append(genre)
                .append("\nRelease Year : ").append(releaseYear)
                .append("\nDuration : ").append(duration)
                .append("\nIs Available ? ").append(available)
                .append("\nAverage Rating : ").append(averageRating())
                .append("\nRating Count : ").append(getRatingCount())
                .append("\n")
                .toString();
    }
}
