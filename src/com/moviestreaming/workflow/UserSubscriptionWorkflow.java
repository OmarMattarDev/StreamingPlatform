
package com.moviestreaming.workflow;

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

public class UserSubscriptionWorkflow {

    private final Connection connection;
    private final UserDAO userDAO;
    private final SubscriptionPlanDAO subscriptionPlanDAO;
    private final UserSubscriptionDAO userSubscriptionDAO;

    public UserSubscriptionWorkflow(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.subscriptionPlanDAO = new SubscriptionPlanDAO(connection);
        this.userSubscriptionDAO = new UserSubscriptionDAO(connection);
    }

    public Integer subscribeUser(UserSubscription subscription)
            throws SQLException {

        if (subscription == null) {
            throw new IllegalArgumentException(
                    "Subscription cannot be null."
            );
        }

        validateId(subscription.getUserId(), "User ID");
        validateId(subscription.getPlanId(), "Plan ID");

        if (subscription.getSubscriptionStatus() !=
                SubscriptionStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "New subscription must have ACTIVE status."
            );
        }

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    User user =
                            userDAO.getUserById(subscription.getUserId());

                    if (user == null) {
                        throw new IllegalArgumentException(
                                "User does not exist."
                        );
                    }

                    if (user.getUserStatus() != UserStatus.ACTIVE) {
                        throw new IllegalStateException(
                                "Only active users can subscribe."
                        );
                    }

                    SubscriptionPlan plan =
                            subscriptionPlanDAO.getPlanById(
                                    subscription.getPlanId()
                            );

                    if (plan == null) {
                        throw new IllegalArgumentException(
                                "Subscription plan does not exist."
                        );
                    }

                    if (!plan.isActive()) {
                        throw new IllegalStateException(
                                "Subscription plan is not active."
                        );
                    }

                    UserSubscription activeSubscription =
                            userSubscriptionDAO.getActiveSubscriptionByUserId(
                                    subscription.getUserId()
                            );

                    if (activeSubscription != null) {
                        throw new IllegalStateException(
                                "User already has an active subscription."
                        );
                    }

                    return userSubscriptionDAO.addSubscription(subscription);
                }
        );
    }

    public boolean cancelSubscription(Integer subscriptionId)
            throws SQLException {

        validateId(subscriptionId, "Subscription ID");

        return WorkflowTransaction.execute(
                connection,
                () -> {

                    UserSubscription subscription =
                            userSubscriptionDAO.getSubscriptionById(
                                    subscriptionId
                            );

                    if (subscription == null) {
                        return false;
                    }

                    if (subscription.getSubscriptionStatus() ==
                            SubscriptionStatus.CANCELLED) {
                        return false;
                    }

                    if (subscription.getSubscriptionStatus() ==
                            SubscriptionStatus.EXPIRED) {
                        throw new IllegalStateException(
                                "Expired subscription cannot be cancelled."
                        );
                    }

                    subscription.cancel();

                    return userSubscriptionDAO.updateSubscriptionStatus(
                            subscriptionId,
                            SubscriptionStatus.CANCELLED
                    );
                }
        );
    }

    private void validateId(Integer id, String fieldName) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}
