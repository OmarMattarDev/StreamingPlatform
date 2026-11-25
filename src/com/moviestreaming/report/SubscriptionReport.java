
package com.moviestreaming.report;

import com.moviestreaming.dao.UserSubscriptionDAO;
import com.moviestreaming.model.UserSubscription;
import com.moviestreaming.model.enums.SubscriptionStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubscriptionReport {

    private final UserSubscriptionDAO userSubscriptionDAO;

    public SubscriptionReport(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        this.userSubscriptionDAO =
                new UserSubscriptionDAO(connection);
    }

    public int getTotalSubscriptions() throws SQLException {
        return userSubscriptionDAO.getAllSubscriptions().size();
    }

    public long getActiveSubscriptionsCount()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions()
                .stream()
                .filter(subscription ->
                        subscription.getSubscriptionStatus()
                                == SubscriptionStatus.ACTIVE
                )
                .count();
    }

    public long getExpiredSubscriptionsCount()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions()
                .stream()
                .filter(subscription ->
                        subscription.getSubscriptionStatus()
                                == SubscriptionStatus.EXPIRED
                )
                .count();
    }

    public long getCancelledSubscriptionsCount()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions()
                .stream()
                .filter(subscription ->
                        subscription.getSubscriptionStatus()
                                == SubscriptionStatus.CANCELLED
                )
                .count();
    }

    public Map<Integer, Long> getSubscriptionsCountByPlan()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                UserSubscription::getPlanId,
                                Collectors.counting()
                        )
                );
    }

    public Map<Integer, Long> getActiveSubscriptionsCountByPlan()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions()
                .stream()
                .filter(subscription ->
                        subscription.getSubscriptionStatus()
                                == SubscriptionStatus.ACTIVE
                )
                .collect(
                        Collectors.groupingBy(
                                UserSubscription::getPlanId,
                                Collectors.counting()
                        )
                );
    }

    public List<UserSubscription> getSubscriptionsSortedByDate()
            throws SQLException {

        return userSubscriptionDAO.getAllSubscriptions()
                .stream()
                .sorted(
                        Comparator.comparing(
                                UserSubscription::getCreatedAt
                        ).reversed()
                )
                .toList();
    }
}
