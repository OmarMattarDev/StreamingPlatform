
package com.moviestreaming.service;

import com.moviestreaming.dao.SubscriptionPlanDAO;
import com.moviestreaming.dao.UserDAO;
import com.moviestreaming.dao.UserSubscriptionDAO;
import com.moviestreaming.model.SubscriptionPlan;
import com.moviestreaming.model.User;
import com.moviestreaming.model.UserSubscription;
import com.moviestreaming.model.enums.SubscriptionStatus;
import com.moviestreaming.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserSubscriptionService {

    private final Connection connection;
    private final UserSubscriptionDAO userSubscriptionDAO;
    private final UserDAO userDAO;
    private final SubscriptionPlanDAO subscriptionPlanDAO;

    public UserSubscriptionService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.userSubscriptionDAO = new UserSubscriptionDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.subscriptionPlanDAO = new SubscriptionPlanDAO(connection);
    }

    public int addSubscription(UserSubscription subscription)
            throws SQLException {

        validateSubscription(subscription);

        return executeTransaction(() -> {

            User user = userDAO.getUserById(subscription.getUserId());

            if (user == null) {
                throw new IllegalArgumentException("User does not exist.");
            }

            if (user.getUserStatus() != UserStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Only active users can subscribe."
                );
            }

            SubscriptionPlan plan =
                    subscriptionPlanDAO.getPlanById(subscription.getPlanId());

            if (plan == null) {
                throw new IllegalArgumentException(
                        "Subscription plan does not exist."
                );
            }

            if (!plan.isActive()) {
                throw new IllegalArgumentException(
                        "This subscription plan is not active."
                );
            }

            UserSubscription activeSubscription =
                    userSubscriptionDAO.getActiveSubscriptionByUserId(
                            subscription.getUserId()
                    );

            if (activeSubscription != null) {
                throw new IllegalArgumentException(
                        "User already has an active subscription."
                );
            }

            return userSubscriptionDAO.addSubscription(subscription);
        });
    }

    public UserSubscription getSubscriptionById(Integer subscriptionId)
            throws SQLException {

        validateId(subscriptionId, "Subscription ID");

        return userSubscriptionDAO.getSubscriptionById(subscriptionId);
    }

    public List<UserSubscription> getSubscriptionsByUser(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return userSubscriptionDAO.getSubscriptionsByUserId(userId);
    }

    public List<UserSubscription> getSubscriptionsByPlan(Integer planId)
            throws SQLException {

        validateId(planId, "Plan ID");

        return userSubscriptionDAO.getSubscriptionsByPlanId(planId);
    }

    public UserSubscription getActiveSubscriptionByUser(Integer userId)
            throws SQLException {

        validateId(userId, "User ID");

        return userSubscriptionDAO.getActiveSubscriptionByUserId(userId);
    }

    public List<UserSubscription> getAllSubscriptions()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions();
    }

    public boolean updateSubscription(UserSubscription subscription)
            throws SQLException {

        validateSubscription(subscription);
        validateId(subscription.getSubscriptionId(), "Subscription ID");

        return executeTransaction(() -> {

            UserSubscription existingSubscription =
                    userSubscriptionDAO.getSubscriptionById(
                            subscription.getSubscriptionId()
                    );

            if (existingSubscription == null) {
                return false;
            }

            if (!existingSubscription.getUserId().equals(
                    subscription.getUserId()) ||
                    !existingSubscription.getPlanId().equals(
                            subscription.getPlanId())) {

                throw new IllegalArgumentException(
                        "User ID and Plan ID cannot be changed."
                );
            }

            return userSubscriptionDAO.updateSubscription(subscription);
        });
    }

    public boolean cancelSubscription(Integer subscriptionId)
            throws SQLException {

        validateId(subscriptionId, "Subscription ID");

        return executeTransaction(() -> {

            UserSubscription subscription =
                    userSubscriptionDAO.getSubscriptionById(subscriptionId);

            if (subscription == null) {
                return false;
            }

            if (subscription.getSubscriptionStatus() ==
                    SubscriptionStatus.CANCELLED) {
                return false;
            }

            subscription.cancel();

            return userSubscriptionDAO.updateSubscriptionStatus(
                    subscriptionId,
                    SubscriptionStatus.CANCELLED
            );
        });
    }

    public boolean expireSubscription(Integer subscriptionId)
            throws SQLException {

        validateId(subscriptionId, "Subscription ID");

        return executeTransaction(() -> {

            UserSubscription subscription =
                    userSubscriptionDAO.getSubscriptionById(subscriptionId);

            if (subscription == null) {
                return false;
            }

            if (subscription.getSubscriptionStatus() !=
                    SubscriptionStatus.ACTIVE) {
                return false;
            }

            if (!LocalDate.now().isAfter(subscription.getEndDate())) {
                throw new IllegalArgumentException(
                        "Subscription has not expired yet."
                );
            }

            subscription.expire();

            return userSubscriptionDAO.updateSubscriptionStatus(
                    subscriptionId,
                    SubscriptionStatus.EXPIRED
            );
        });
    }

    public boolean deleteSubscription(Integer subscriptionId)
            throws SQLException {

        validateId(subscriptionId, "Subscription ID");

        return executeTransaction(
                () -> userSubscriptionDAO.deleteSubscription(subscriptionId)
        );
    }

    public List<UserSubscription> getExpiredSubscriptions()
            throws SQLException {

        return getAllSubscriptions().stream()
                .filter(subscription ->
                        subscription.getSubscriptionStatus() ==
                                SubscriptionStatus.EXPIRED ||
                                (subscription.getSubscriptionStatus() ==
                                        SubscriptionStatus.ACTIVE &&
                                        LocalDate.now().isAfter(
                                                subscription.getEndDate()
                                        ))
                )
                .toList();
    }

    private void validateSubscription(UserSubscription subscription) {
        if (subscription == null) {
            throw new IllegalArgumentException(
                    "Subscription cannot be null."
            );
        }

        validateId(subscription.getUserId(), "User ID");
        validateId(subscription.getPlanId(), "Plan ID");
    }

    private void validateId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
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
