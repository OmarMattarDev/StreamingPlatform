
package com.moviestreaming.dao;

import com.moviestreaming.model.UserSubscription;
import com.moviestreaming.model.enums.SubscriptionStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserSubscriptionDAO {

    private final Connection connection;

    public UserSubscriptionDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Subscription
    public int addSubscription(UserSubscription subscription) throws SQLException {

        if (subscription == null) {
            throw new IllegalArgumentException("Subscription cannot be null");
        }

        String sql = """
                INSERT INTO user_subscriptions
                (user_id, plan_id, start_date, end_date, subscription_status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, subscription.getUserId());
            ps.setInt(2, subscription.getPlanId());
            ps.setDate(3, Date.valueOf(subscription.getStartDate()));
            ps.setDate(4, Date.valueOf(subscription.getEndDate()));
            ps.setString(5, subscription.getSubscriptionStatus().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating subscription failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Creating subscription failed, no ID obtained");
            }
        }
    }

    // Get Subscription By ID
    public UserSubscription getSubscriptionById(int subscriptionId)
            throws SQLException {

        if (subscriptionId <= 0) {
            throw new IllegalArgumentException("Subscription ID must be positive");
        }

        String sql = """
                SELECT *
                FROM user_subscriptions
                WHERE subscription_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, subscriptionId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToSubscription(resultSet);
                }

                return null;
            }
        }
    }

    // Get Subscriptions By User ID
    public List<UserSubscription> getSubscriptionsByUserId(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                SELECT *
                FROM user_subscriptions
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        List<UserSubscription> subscriptions = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    subscriptions.add(mapResultSetToSubscription(resultSet));
                }
            }
        }

        return subscriptions;
    }

    // Get Subscriptions By Plan ID
    public List<UserSubscription> getSubscriptionsByPlanId(int planId)
            throws SQLException {

        if (planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }

        String sql = """
                SELECT *
                FROM user_subscriptions
                WHERE plan_id = ?
                ORDER BY created_at DESC
                """;

        List<UserSubscription> subscriptions = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, planId);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    subscriptions.add(mapResultSetToSubscription(resultSet));
                }
            }
        }

        return subscriptions;
    }

    // Get Active Subscription By User ID
    public UserSubscription getActiveSubscriptionByUserId(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        String sql = """
                SELECT *
                FROM user_subscriptions
                WHERE user_id = ?
                  AND subscription_status = 'ACTIVE'
                  AND start_date <= CURRENT_DATE
                  AND end_date >= CURRENT_DATE
                ORDER BY end_date DESC
                LIMIT 1
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToSubscription(resultSet);
                }

                return null;
            }
        }
    }

    // Get All Subscriptions
    public List<UserSubscription> getAllSubscriptions() throws SQLException {

        String sql = """
                SELECT *
                FROM user_subscriptions
                ORDER BY created_at DESC
                """;

        List<UserSubscription> subscriptions = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                subscriptions.add(mapResultSetToSubscription(resultSet));
            }
        }

        return subscriptions;
    }

    // Update Subscription
    public boolean updateSubscription(UserSubscription subscription)
            throws SQLException {

        if (subscription == null) {
            throw new IllegalArgumentException("Subscription cannot be null");
        }

        if (subscription.getSubscriptionId() == null
                || subscription.getSubscriptionId() <= 0) {
            throw new IllegalArgumentException("Invalid subscription ID");
        }

        String sql = """
                UPDATE user_subscriptions
                SET user_id = ?,
                    plan_id = ?,
                    start_date = ?,
                    end_date = ?,
                    subscription_status = ?
                WHERE subscription_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, subscription.getUserId());
            ps.setInt(2, subscription.getPlanId());
            ps.setDate(3, Date.valueOf(subscription.getStartDate()));
            ps.setDate(4, Date.valueOf(subscription.getEndDate()));
            ps.setString(5, subscription.getSubscriptionStatus().name());
            ps.setInt(6, subscription.getSubscriptionId());

            return ps.executeUpdate() > 0;
        }
    }

    // Update Subscription Status
    public boolean updateSubscriptionStatus(int subscriptionId,
                                            SubscriptionStatus status)
            throws SQLException {

        if (subscriptionId <= 0) {
            throw new IllegalArgumentException("Subscription ID must be positive");
        }

        if (status == null) {
            throw new IllegalArgumentException("Subscription status cannot be null");
        }

        String sql = """
                UPDATE user_subscriptions
                SET subscription_status = ?
                WHERE subscription_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, subscriptionId);

            return ps.executeUpdate() > 0;
        }
    }

    // Delete Subscription
    public boolean deleteSubscription(int subscriptionId) throws SQLException {

        if (subscriptionId <= 0) {
            throw new IllegalArgumentException("Subscription ID must be positive");
        }

        String sql = """
                DELETE FROM user_subscriptions
                WHERE subscription_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, subscriptionId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private UserSubscription mapResultSetToSubscription(ResultSet resultSet)
            throws SQLException {

        Integer subscriptionId = resultSet.getInt("subscription_id");

        Integer userId = resultSet.getInt("user_id");
        Integer planId = resultSet.getInt("plan_id");

        Date startDateValue = resultSet.getDate("start_date");
        Date endDateValue = resultSet.getDate("end_date");

        SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(
                resultSet.getString("subscription_status")
        );

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        return new UserSubscription(
                subscriptionId,
                userId,
                planId,
                startDateValue.toLocalDate(),
                endDateValue.toLocalDate(),
                subscriptionStatus,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
