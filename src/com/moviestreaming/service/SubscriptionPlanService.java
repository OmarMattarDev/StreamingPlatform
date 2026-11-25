
package com.moviestreaming.service;

import com.moviestreaming.dao.SubscriptionPlanDAO;
import com.moviestreaming.model.SubscriptionPlan;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class SubscriptionPlanService {

    private final Connection connection;
    private final SubscriptionPlanDAO subscriptionPlanDAO;

    public SubscriptionPlanService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.subscriptionPlanDAO = new SubscriptionPlanDAO(connection);
    }

    public int addSubscriptionPlan(SubscriptionPlan plan)
            throws SQLException {

        validatePlan(plan);

        if (subscriptionPlanDAO.getPlanByName(plan.getPlanName()) != null) {
            throw new IllegalArgumentException(
                    "Subscription plan name already exists."
            );
        }

        return executeTransaction(
                () -> subscriptionPlanDAO.addPlan(plan)
        );
    }

    public SubscriptionPlan getPlanById(Integer planId)
            throws SQLException {

        validateId(planId, "Plan ID");

        return subscriptionPlanDAO.getPlanById(planId);
    }

    public SubscriptionPlan getPlanByName(String planName)
            throws SQLException {

        validateString(planName, "Plan name");

        return subscriptionPlanDAO.getPlanByName(planName.trim());
    }

    public List<SubscriptionPlan> getAllPlans() throws SQLException {
        return subscriptionPlanDAO.getAllPlans();
    }

    public List<SubscriptionPlan> getActivePlans() throws SQLException {
        return subscriptionPlanDAO.getActivePlans();
    }

    public boolean updateSubscriptionPlan(SubscriptionPlan plan)
            throws SQLException {

        validatePlan(plan);
        validateId(plan.getPlanId(), "Plan ID");

        return executeTransaction(() -> {

            SubscriptionPlan existingPlan =
                    subscriptionPlanDAO.getPlanById(plan.getPlanId());

            if (existingPlan == null) {
                return false;
            }

            SubscriptionPlan planWithSameName =
                    subscriptionPlanDAO.getPlanByName(plan.getPlanName());

            if (planWithSameName != null &&
                    !planWithSameName.getPlanId().equals(plan.getPlanId())) {
                throw new IllegalArgumentException(
                        "Subscription plan name already exists."
                );
            }

            return subscriptionPlanDAO.updatePlan(plan);
        });
    }

    public boolean deleteSubscriptionPlan(Integer planId)
            throws SQLException {

        validateId(planId, "Plan ID");

        return executeTransaction(
                () -> subscriptionPlanDAO.deletePlan(planId)
        );
    }

    public List<SubscriptionPlan> getPlansSortedByPrice()
            throws SQLException {

        return getActivePlans().stream()
                .sorted(Comparator.comparing(SubscriptionPlan::getPrice))
                .toList();
    }

    private void validatePlan(SubscriptionPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException(
                    "Subscription plan cannot be null."
            );
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
