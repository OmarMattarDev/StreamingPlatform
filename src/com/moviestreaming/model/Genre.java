
package com.moviestreaming.model;

import java.util.Objects;

public class Genre {

    // Fields
    private Integer genreId;
    private String genreName;
    private String description;

    // Constructor for new genres
    public Genre(String genreName, String description) {

        setGenreName(genreName);
        setDescription(description);
    }

    // Constructor for database mapping
    public Genre(Integer genreId, String genreName,
                 String description) {

        if (genreId == null || genreId <= 0) {
            throw new IllegalArgumentException("Invalid genre ID.");
        }

        this.genreId = genreId;

        setGenreName(genreName);
        setDescription(description);
    }

    // Getters
    public Integer getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setGenreName(String genreName) {

        if (genreName == null || genreName.isBlank()) {
            throw new IllegalArgumentException(
                    "Genre name cannot be empty.");
        }

        if (genreName.trim().length() > 50) {
            throw new IllegalArgumentException(
                    "Genre name cannot exceed 50 characters.");
        }

        this.genreName = genreName.trim();
    }

    public void setDescription(String description) {

        if (description != null
                && description.trim().length() > 255) {

            throw new IllegalArgumentException(
                    "Genre description cannot exceed 255 characters.");
        }

        this.description = description == null
                ? null : description.trim();
    }

    // Business Methods
    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean matchesName(String name) {

        return name != null
                && genreName.equalsIgnoreCase(name.trim());
    }

    // Object Methods
    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + genreId +
                ", genreName='" + genreName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Genre)) {
            return false;
        }

        Genre genre = (Genre) obj;

        return genreId != null
                && genreId.equals(genre.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(genreId);
    }
}
