
package com.moviestreaming.model;

import com.moviestreaming.model.enums.UserRole;
import com.moviestreaming.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    // Fields
    private Integer userId;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private UserRole userRole;
    private UserStatus userStatus;
    private LocalDateTime createdAt;

    // Constructor for new users
    public User(String username, String email, String passwordHash,
                String firstName, String lastName) {

        setUsername(username);
        setEmail(email);
        setPasswordHash(passwordHash);
        setFirstName(firstName);
        setLastName(lastName);

        this.userRole = UserRole.USER;
        this.userStatus = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for database mapping
    public User(Integer userId, String username, String email,
                String passwordHash, String firstName, String lastName,
                UserRole userRole, UserStatus userStatus,
                LocalDateTime createdAt) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        this.userId = userId;

        setUsername(username);
        setEmail(email);
        setPasswordHash(passwordHash);
        setFirstName(firstName);
        setLastName(lastName);
        setUserRole(userRole);
        setUserStatus(userStatus);

        validateCreatedAt(createdAt);
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty.");
        }

        String trimmedUsername = username.trim();

        if (trimmedUsername.length() < 3
                || trimmedUsername.length() > 50) {

            throw new IllegalArgumentException(
                    "Username must be between 3 and 50 characters.");
        }

        for (int i = 0; i < trimmedUsername.length(); i++) {

            char ch = trimmedUsername.charAt(i);

            if (!Character.isLetterOrDigit(ch) && ch != '_') {
                throw new IllegalArgumentException(
                        "Username can contain only letters, digits and underscores.");
            }
        }

        this.username = trimmedUsername;
    }

    public void setEmail(String email) {

        validateEmail(email);
        this.email = email.trim().toLowerCase();
    }

    public void setPasswordHash(String passwordHash) {

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException(
                    "Password hash cannot be empty.");
        }

        if (passwordHash.length() > 255) {
            throw new IllegalArgumentException(
                    "Password hash cannot exceed 255 characters.");
        }

        this.passwordHash = passwordHash.trim();
    }

    public void setFirstName(String firstName) {

        validateName(firstName, "First name");
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {

        validateName(lastName, "Last name");
        this.lastName = lastName.trim();
    }

    public void setUserRole(UserRole userRole) {

        if (userRole == null) {
            throw new IllegalArgumentException(
                    "User role cannot be null.");
        }

        this.userRole = userRole;
    }

    public void setUserStatus(UserStatus userStatus) {

        if (userStatus == null) {
            throw new IllegalArgumentException(
                    "User status cannot be null.");
        }

        this.userStatus = userStatus;
    }

    // Validation Methods
    private void validateName(String name, String fieldName) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty.");
        }

        if (name.trim().length() < 2
                || name.trim().length() > 50) {

            throw new IllegalArgumentException(
                    fieldName + " must be between 2 and 50 characters.");
        }
    }

    private void validateEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty.");
        }

        String trimmedEmail = email.trim();

        int atIndex = trimmedEmail.indexOf("@");
        int dotIndex = trimmedEmail.lastIndexOf(".");

        if (trimmedEmail.length() > 100
                || atIndex <= 0
                || dotIndex <= atIndex + 1
                || dotIndex == trimmedEmail.length() - 1
                || trimmedEmail.contains(" ")) {

            throw new IllegalArgumentException(
                    "Invalid email address.");
        }
    }

    private void validateCreatedAt(LocalDateTime date) {

        if (date == null || date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Invalid account creation date.");
        }
    }

    // Business Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return userStatus == UserStatus.ACTIVE;
    }

    public boolean isAdmin() {
        return userRole == UserRole.ADMIN;
    }

    public void activate() {
        userStatus = UserStatus.ACTIVE;
    }

    public void suspend() {
        userStatus = UserStatus.SUSPENDED;
    }

    public void closeAccount() {
        userStatus = UserStatus.CLOSED;
    }

    public void promoteToAdmin() {
        userRole = UserRole.ADMIN;
    }

    public void changeToRegularUser() {
        userRole = UserRole.USER;
    }

    // Object Methods
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", userRole=" + userRole +
                ", userStatus=" + userStatus +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User) obj;

        return userId != null
                && userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
