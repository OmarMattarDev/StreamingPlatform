
package com.moviestreaming.model;

import com.moviestreaming.model.enums.MovieStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Movie {

    // Fields
    private Integer movieId;
    private String title;
    private String description;
    private Integer releaseYear;
    private Integer durationMinutes;
    private String ageRating;
    private String language;
    private String country;
    private String director;
    private MovieStatus movieStatus;
    private LocalDateTime createdAt;

    // Constructor for new movies
    public Movie(String title, String description, Integer releaseYear,
                 Integer durationMinutes, String ageRating,
                 String language, String country, String director) {

        setTitle(title);
        setDescription(description);
        setReleaseYear(releaseYear);
        setDurationMinutes(durationMinutes);
        setAgeRating(ageRating);
        setLanguage(language);
        setCountry(country);
        setDirector(director);

        this.movieStatus = MovieStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for database mapping
    public Movie(Integer movieId, String title, String description,
                 Integer releaseYear, Integer durationMinutes,
                 String ageRating, String language, String country,
                 String director, MovieStatus movieStatus,
                 LocalDateTime createdAt) {

        if (movieId == null || movieId <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        this.movieId = movieId;

        setTitle(title);
        setDescription(description);
        setReleaseYear(releaseYear);
        setDurationMinutes(durationMinutes);
        setAgeRating(ageRating);
        setLanguage(language);
        setCountry(country);
        setDirector(director);
        setMovieStatus(movieStatus);

        validateCreatedAt(createdAt);
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getDirector() {
        return director;
    }

    public MovieStatus getMovieStatus() {
        return movieStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Movie title cannot be empty.");
        }

        if (title.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Movie title cannot exceed 150 characters.");
        }

        this.title = title.trim();
    }

    public void setDescription(String description) {

        if (description != null
                && description.length() > 65535) {

            throw new IllegalArgumentException(
                    "Movie description is too long.");
        }

        this.description = description == null
                ? null : description.trim();
    }

    public void setReleaseYear(Integer releaseYear) {

        if (releaseYear != null
                && (releaseYear < 1888 || releaseYear > 2100)) {

            throw new IllegalArgumentException(
                    "Invalid movie release year.");
        }

        this.releaseYear = releaseYear;
    }

    public void setDurationMinutes(Integer durationMinutes) {

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Movie duration must be greater than zero.");
        }

        this.durationMinutes = durationMinutes;
    }

    public void setAgeRating(String ageRating) {

        if (ageRating != null && ageRating.isBlank()) {
            throw new IllegalArgumentException(
                    "Age rating cannot be blank.");
        }

        if (ageRating != null && ageRating.trim().length() > 10) {
            throw new IllegalArgumentException(
                    "Age rating cannot exceed 10 characters.");
        }

        this.ageRating = ageRating == null
                ? null : ageRating.trim().toUpperCase();
    }

    public void setLanguage(String language) {

        validateOptionalText(language, 50, "Language");
        this.language = language == null ? null : language.trim();
    }

    public void setCountry(String country) {

        validateOptionalText(country, 60, "Country");
        this.country = country == null ? null : country.trim();
    }

    public void setDirector(String director) {

        validateOptionalText(director, 100, "Director");
        this.director = director == null ? null : director.trim();
    }

    public void setMovieStatus(MovieStatus movieStatus) {

        if (movieStatus == null) {
            throw new IllegalArgumentException(
                    "Movie status cannot be null.");
        }

        this.movieStatus = movieStatus;
    }

    // Validation Methods
    private void validateOptionalText(String value,
                                      int maxLength,
                                      String fieldName) {

        if (value != null
                && (value.isBlank()
                || value.trim().length() > maxLength)) {

            throw new IllegalArgumentException(
                    "Invalid " + fieldName.toLowerCase() + ".");
        }
    }

    private void validateCreatedAt(LocalDateTime date) {

        if (date == null || date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Invalid movie creation date.");
        }
    }

    // Business Methods
    public boolean isAvailable() {
        return movieStatus == MovieStatus.AVAILABLE;
    }

    public void makeAvailable() {
        movieStatus = MovieStatus.AVAILABLE;
    }

    public void makeUnavailable() {
        movieStatus = MovieStatus.UNAVAILABLE;
    }

    public boolean wasReleasedBefore(int year) {

        if (releaseYear == null) {
            return false;
        }

        return releaseYear < year;
    }

    public boolean isLongMovie() {
        return durationMinutes >= 150;
    }

    public int getDurationHours() {
        return durationMinutes / 60;
    }

    public int getRemainingMinutes() {
        return durationMinutes % 60;
    }

    public String getFormattedDuration() {

        return getDurationHours() + "h "
                + getRemainingMinutes() + "m";
    }

    // Object Methods
    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", durationMinutes=" + durationMinutes +
                ", ageRating='" + ageRating + '\'' +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                ", director='" + director + '\'' +
                ", movieStatus=" + movieStatus +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Movie)) {
            return false;
        }

        Movie movie = (Movie) obj;

        return movieId != null
                && movieId.equals(movie.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(movieId);
    }
}
