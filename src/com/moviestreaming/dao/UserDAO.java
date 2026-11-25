
package com.moviestreaming.dao;

import com.moviestreaming.model.User;
import com.moviestreaming.model.enums.UserRole;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add User
    public int addUser(User user) throws SQLException {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String sql = """
                INSERT INTO users
                (username, email, password_hash, first_name, last_name, role, account_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getUserRole().name());
            ps.setString(7, user.getUserStatus().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Creating user failed, no ID obtained");
            }
        }
    }

    // Get User By ID
    public User getUserById(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                SELECT *
                FROM users
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }

                return null;
            }
        }
    }

    // Get User By Username
    public User getUserByUsername(String username) throws SQLException {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        String sql = """
                SELECT *
                FROM users
                WHERE username = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }

                return null;
            }
        }
    }

    // Get User By Email
    public User getUserByEmail(String email) throws SQLException {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String sql = """
                SELECT *
                FROM users
                WHERE email = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }

                return null;
            }
        }
    }

    // Get All Users
    public List<User> getAllUsers() throws SQLException {

        String sql = """
                SELECT *
                FROM users
                ORDER BY user_id
                """;

        List<User> users = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        }

        return users;
    }

    // Get Users By Status
    public List<User> getUsersByStatus(UserStatus status) throws SQLException {

        if (status == null) {
            throw new IllegalArgumentException("User status cannot be null");
        }

        String sql = """
                SELECT *
                FROM users
                WHERE account_status = ?
                """;

        List<User> users = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        }

        return users;
    }

    // Update User
    public boolean updateUser(User user) throws SQLException {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getUserId() == null || user.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        String sql = """
                UPDATE users
                SET username = ?,
                    email = ?,
                    password_hash = ?,
                    first_name = ?,
                    last_name = ?,
                    role = ?,
                    account_status = ?
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getUserRole().name());
            ps.setString(7, user.getUserStatus().name());
            ps.setInt(8, user.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete User
    public boolean deleteUser(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {

        Integer userId = resultSet.getInt("user_id");

        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String passwordHash = resultSet.getString("password_hash");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");

        UserRole userRole = UserRole.valueOf(
                resultSet.getString("role")
        );

        UserStatus userStatus = UserStatus.valueOf(
                resultSet.getString("account_status")
        );

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        return new User(
                userId,
                username,
                email,
                passwordHash,
                firstName,
                lastName,
                userRole,
                userStatus,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
