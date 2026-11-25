
package com.moviestreaming.report;

import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.model.User;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class UserReport {

    private final UserDAO userDAO;

    public UserReport(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.userDAO = new UserDAO(connection);
    }

    public int getTotalUsers() throws SQLException {
        return userDAO.getAllUsers().size();
    }

    public long getActiveUsersCount() throws SQLException {
        return userDAO.getAllUsers()
                .stream()
                .filter(user ->
                        user.getUserStatus() == UserStatus.ACTIVE
                )
                .count();
    }

    public long getSuspendedUsersCount() throws SQLException {
        return userDAO.getAllUsers()
                .stream()
                .filter(user ->
                        user.getUserStatus() == UserStatus.SUSPENDED
                )
                .count();
    }

    public long getClosedUsersCount() throws SQLException {
        return userDAO.getAllUsers()
                .stream()
                .filter(user ->
                        user.getUserStatus() == UserStatus.CLOSED
                )
                .count();
    }

    public long getAdministratorsCount() throws SQLException {
        return userDAO.getAllUsers()
                .stream()
                .filter(User::isAdmin)
                .count();
    }

    public List<User> getUsersSortedByUsername()
            throws SQLException {

        return userDAO.getAllUsers()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getUsername,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }

    public List<User> getActiveUsers()
            throws SQLException {

        return userDAO.getAllUsers()
                .stream()
                .filter(user ->
                        user.getUserStatus() == UserStatus.ACTIVE
                )
                .toList();
    }
}
