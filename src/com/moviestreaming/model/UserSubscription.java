
package com.moviestreaming.model;

import com.moviestreaming.model.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class UserSubscription {

    // Fields
    private Integer subscriptionId;
    private Integer userId;
    private Integer planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime createdAt;

    // Constructor for new subscriptions
    public UserSubscription(Integer userId, Integer planId,
                            LocalDate startDate, LocalDate endDate) {

        setUserId(userId);
        setPlanId(planId);
        setStartDate(startDate);
        setEndDate(endDate);

        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for database mapping
    public UserSubscription(Integer subscriptionId, Integer userId,
                            Integer planId, LocalDate startDate,
                            LocalDate endDate,
                            SubscriptionStatus subscriptionStatus,
                            LocalDateTime createdAt) {

        if (subscriptionId == null || subscriptionId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid subscription ID.");
        }

        this.subscriptionId = subscriptionId;

        setUserId(userId);
        setPlanId(planId);
        setStartDate(startDate);
        setEndDate(endDate);
        setSubscriptionStatus(subscriptionStatus);

        validateCreatedAt(createdAt);
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUserId(Integer userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        this.userId = userId;
    }

    public void setPlanId(Integer planId) {

        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Invalid plan ID.");
        }

        this.planId = planId;
    }

    public void setStartDate(LocalDate startDate) {

        if (startDate == null) {
            throw new IllegalArgumentException(
                    "Subscription start date cannot be null.");
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }

        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {

        if (endDate == null) {
            throw new IllegalArgumentException(
                    "Subscription end date cannot be null.");
        }

        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }

        this.endDate = endDate;
    }

    public void setSubscriptionStatus(
            SubscriptionStatus subscriptionStatus) {

        if (subscriptionStatus == null) {
            throw new IllegalArgumentException(
                    "Subscription status cannot be null.");
        }

        this.subscriptionStatus = subscriptionStatus;
    }

    // Validation Methods
    private void validateCreatedAt(LocalDateTime date) {

        if (date == null || date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Invalid subscription creation date.");
        }
    }

    // Business Methods
    public boolean isActive() {

        return subscriptionStatus == SubscriptionStatus.ACTIVE
                && !LocalDate.now().isBefore(startDate)
                && !LocalDate.now().isAfter(endDate);
    }

    public boolean isExpired() {

        return subscriptionStatus == SubscriptionStatus.EXPIRED
                || LocalDate.now().isAfter(endDate);
    }

    public boolean isCancelled() {

        return subscriptionStatus == SubscriptionStatus.CANCELLED;
    }

    public void cancel() {

        if (subscriptionStatus != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active subscriptions can be cancelled.");
        }

        subscriptionStatus = SubscriptionStatus.CANCELLED;
    }

    public void expire() {
        subscriptionStatus = SubscriptionStatus.EXPIRED;
    }

    public long getRemainingDays() {

        if (isExpired() || isCancelled()
                || LocalDate.now().isBefore(startDate)) {
            return 0;
        }

        return Math.max(
                0, ChronoUnit.DAYS.between(
                        LocalDate.now(), endDate));
    }

    // Object Methods
    @Override
    public String toString() {
        return "UserSubscription{" +
                "subscriptionId=" + subscriptionId +
                ", userId=" + userId +
                ", planId=" + planId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", subscriptionStatus=" + subscriptionStatus +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UserSubscription)) {
            return false;
        }

        UserSubscription subscription = (UserSubscription) obj;

        return subscriptionId != null
                && subscriptionId.equals(subscription.subscriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subscriptionId);
    }
}
