
package com.moviestreaming.dao;

import com.moviestreaming.model.SubscriptionPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlanDAO {

    private final Connection connection;

    public SubscriptionPlanDAO(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    // Add Subscription Plan
    public int addPlan(SubscriptionPlan plan) throws SQLException {

        if (plan == null) {
            throw new IllegalArgumentException("Subscription plan cannot be null");
        }

        String sql = """
                INSERT INTO subscription_plans
                (plan_name, price, duration_days, max_devices,
                 plan_description, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, plan.getPlanName());
            ps.setBigDecimal(2, plan.getPrice());
            ps.setInt(3, plan.getDurationDays());
            ps.setInt(4, plan.getMaxDevices());
            ps.setString(5, plan.getPlanDescription());
            ps.setBoolean(6, plan.isActive());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating subscription plan failed");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException(
                        "Creating subscription plan failed, no ID obtained"
                );
            }
        }
    }

    // Get Plan By ID
    public SubscriptionPlan getPlanById(int planId) throws SQLException {

        if (planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }

        String sql = """
                SELECT *
                FROM subscription_plans
                WHERE plan_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, planId);

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToPlan(resultSet);
                }

                return null;
            }
        }
    }

    // Get Plan By Name
    public SubscriptionPlan getPlanByName(String planName) throws SQLException {

        if (planName == null || planName.isBlank()) {
            throw new IllegalArgumentException("Plan name cannot be null or empty");
        }

        String sql = """
                SELECT *
                FROM subscription_plans
                WHERE plan_name = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, planName.trim());

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToPlan(resultSet);
                }

                return null;
            }
        }
    }

    // Get All Plans
    public List<SubscriptionPlan> getAllPlans() throws SQLException {

        String sql = """
                SELECT *
                FROM subscription_plans
                ORDER BY price
                """;

        List<SubscriptionPlan> plans = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                plans.add(mapResultSetToPlan(resultSet));
            }
        }

        return plans;
    }

    // Get Active Plans
    public List<SubscriptionPlan> getActivePlans() throws SQLException {

        String sql = """
                SELECT *
                FROM subscription_plans
                WHERE is_active = TRUE
                ORDER BY price
                """;

        List<SubscriptionPlan> plans = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                plans.add(mapResultSetToPlan(resultSet));
            }
        }

        return plans;
    }

    // Update Plan
    public boolean updatePlan(SubscriptionPlan plan) throws SQLException {

        if (plan == null) {
            throw new IllegalArgumentException("Subscription plan cannot be null");
        }

        if (plan.getPlanId() == null || plan.getPlanId() <= 0) {
            throw new IllegalArgumentException("Invalid plan ID");
        }

        String sql = """
                UPDATE subscription_plans
                SET plan_name = ?,
                    price = ?,
                    duration_days = ?,
                    max_devices = ?,
                    plan_description = ?,
                    is_active = ?
                WHERE plan_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, plan.getPlanName());
            ps.setBigDecimal(2, plan.getPrice());
            ps.setInt(3, plan.getDurationDays());
            ps.setInt(4, plan.getMaxDevices());
            ps.setString(5, plan.getPlanDescription());
            ps.setBoolean(6, plan.isActive());
            ps.setInt(7, plan.getPlanId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete Plan
    public boolean deletePlan(int planId) throws SQLException {

        if (planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }

        String sql = """
                DELETE FROM subscription_plans
                WHERE plan_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, planId);

            return ps.executeUpdate() > 0;
        }
    }

    // ResultSet Mapping
    private SubscriptionPlan mapResultSetToPlan(ResultSet resultSet)
            throws SQLException {

        Integer planId = resultSet.getInt("plan_id");

        String planName = resultSet.getString("plan_name");
        java.math.BigDecimal price = resultSet.getBigDecimal("price");

        int durationDays = resultSet.getInt("duration_days");
        int maxDevices = resultSet.getInt("max_devices");

        String planDescription = resultSet.getString("plan_description");
        boolean active = resultSet.getBoolean("is_active");

        return new SubscriptionPlan(
                planId,
                planName,
                price,
                durationDays,
                maxDevices,
                planDescription,
                active
        );
    }
}
