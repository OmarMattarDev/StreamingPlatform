
package com.moviestreaming.service;

import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.model.User;
import com.moviestreaming.model.enums.UserRole;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final Connection connection;
    private final UserDAO userDAO;

    public UserService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.userDAO = new UserDAO(connection);
    }

    public int registerUser(User user) throws SQLException {
        validateUser(user);

        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (userDAO.getUserByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists.");
        }

        return executeTransaction(() -> userDAO.addUser(user));
    }

    public User getUserById(Integer userId) throws SQLException {
        validateId(userId, "User ID");

        return userDAO.getUserById(userId);
    }

    public User getUserByUsername(String username) throws SQLException {
        validateString(username, "Username");

        return userDAO.getUserByUsername(username.trim());
    }

    public User getUserByEmail(String email) throws SQLException {
        validateString(email, "Email");

        return userDAO.getUserByEmail(email.trim());
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public List<User> getUsersByStatus(UserStatus status)
            throws SQLException {

        if (status == null) {
            throw new IllegalArgumentException("User status cannot be null.");
        }

        return userDAO.getUsersByStatus(status);
    }

    public boolean updateUser(User user) throws SQLException {
        validateUser(user);
        validateId(user.getUserId(), "User ID");

        return executeTransaction(() -> {

            User existingUser = userDAO.getUserById(user.getUserId());

            if (existingUser == null) {
                return false;
            }

            User usernameOwner =
                    userDAO.getUserByUsername(user.getUsername());

            if (usernameOwner != null &&
                    !usernameOwner.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException(
                        "Username already exists."
                );
            }

            User emailOwner =
                    userDAO.getUserByEmail(user.getEmail());

            if (emailOwner != null &&
                    !emailOwner.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException(
                        "Email already exists."
                );
            }

            return userDAO.updateUser(user);
        });
    }

    public boolean deleteUser(Integer userId) throws SQLException {
        validateId(userId, "User ID");

        return executeTransaction(() -> userDAO.deleteUser(userId));
    }

    public boolean activateUser(Integer userId) throws SQLException {
        validateId(userId, "User ID");

        return executeTransaction(() -> {

            User user = userDAO.getUserById(userId);

            if (user == null) {
                return false;
            }

            if (user.getUserStatus() == UserStatus.CLOSED) {
                throw new IllegalArgumentException(
                        "Closed accounts cannot be activated."
                );
            }

            user.activate();

            return userDAO.updateUser(user);
        });
    }

    public boolean suspendUser(Integer userId) throws SQLException {
        validateId(userId, "User ID");

        return executeTransaction(() -> {

            User user = userDAO.getUserById(userId);

            if (user == null) {
                return false;
            }

            if (user.getUserStatus() == UserStatus.CLOSED) {
                throw new IllegalArgumentException(
                        "Closed accounts cannot be suspended."
                );
            }

            user.suspend();

            return userDAO.updateUser(user);
        });
    }

    public boolean closeUserAccount(Integer userId) throws SQLException {
        validateId(userId, "User ID");

        return executeTransaction(() -> {

            User user = userDAO.getUserById(userId);

            if (user == null) {
                return false;
            }

            user.closeAccount();

            return userDAO.updateUser(user);
        });
    }

    public boolean promoteToAdmin(Integer userId) throws SQLException {
        validateId(userId, "User ID");

        return executeTransaction(() -> {

            User user = userDAO.getUserById(userId);

            if (user == null) {
                return false;
            }

            if (user.getUserStatus() != UserStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Only active users can be promoted."
                );
            }

            user.promoteToAdmin();

            return userDAO.updateUser(user);
        });
    }

    public List<User> getActiveUsers() throws SQLException {
        return getAllUsers().stream()
                .filter(User::isActive)
                .toList();
    }

    public List<User> getAdministrators() throws SQLException {
        return getAllUsers().stream()
                .filter(user -> user.getUserRole() == UserRole.ADMIN)
                .toList();
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }

    private void validateId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }

    private void validateString(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
            );
        }
    }

    private <T> T executeTransaction(SqlOperation<T> operation)
            throws SQLException {

        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            T result = operation.execute();

            connection.commit();

            return result;

        } catch (SQLException | RuntimeException exception) {
            connection.rollback();

            throw exception;

        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @FunctionalInterface
    private interface SqlOperation<T> {
        T execute() throws SQLException;
    }
}
