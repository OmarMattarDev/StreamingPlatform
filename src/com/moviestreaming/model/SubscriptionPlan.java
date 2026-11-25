
package com.moviestreaming.model;

import java.math.BigDecimal;
import java.util.Objects;

public class SubscriptionPlan {

    // Fields
    private Integer planId;
    private String planName;
    private BigDecimal price;
    private Integer durationDays;
    private Integer maxDevices;
    private String planDescription;
    private boolean active;

    // Constructor for new subscription plans
    public SubscriptionPlan(String planName, BigDecimal price,
                            Integer durationDays, Integer maxDevices,
                            String planDescription) {

        setPlanName(planName);
        setPrice(price);
        setDurationDays(durationDays);
        setMaxDevices(maxDevices);
        setPlanDescription(planDescription);

        this.active = true;
    }

    // Constructor for database mapping
    public SubscriptionPlan(Integer planId, String planName,
                            BigDecimal price, Integer durationDays,
                            Integer maxDevices, String planDescription,
                            boolean active) {

        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Invalid plan ID.");
        }

        this.planId = planId;

        setPlanName(planName);
        setPrice(price);
        setDurationDays(durationDays);
        setMaxDevices(maxDevices);
        setPlanDescription(planDescription);

        this.active = active;
    }

    // Getters
    public Integer getPlanId() {
        return planId;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public Integer getMaxDevices() {
        return maxDevices;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setPlanName(String planName) {

        if (planName == null || planName.isBlank()) {
            throw new IllegalArgumentException(
                    "Plan name cannot be empty.");
        }

        if (planName.trim().length() > 50) {
            throw new IllegalArgumentException(
                    "Plan name cannot exceed 50 characters.");
        }

        this.planName = planName.trim();
    }

    public void setPrice(BigDecimal price) {

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Plan price cannot be negative or null.");
        }

        if (price.scale() > 2 || price.precision() > 10) {
            throw new IllegalArgumentException(
                    "Plan price must fit DECIMAL(10,2).");
        }

        this.price = price;
    }

    public void setDurationDays(Integer durationDays) {

        if (durationDays == null || durationDays <= 0) {
            throw new IllegalArgumentException(
                    "Plan duration must be greater than zero.");
        }

        this.durationDays = durationDays;
    }

    public void setMaxDevices(Integer maxDevices) {

        if (maxDevices == null || maxDevices <= 0) {
            throw new IllegalArgumentException(
                    "Maximum devices must be greater than zero.");
        }

        this.maxDevices = maxDevices;
    }

    public void setPlanDescription(String planDescription) {

        if (planDescription != null
                && planDescription.trim().length() > 255) {

            throw new IllegalArgumentException(
                    "Plan description cannot exceed 255 characters.");
        }

        this.planDescription = planDescription == null
                ? null : planDescription.trim();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Business Methods
    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isFreePlan() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isAvailableForSubscription() {
        return active;
    }

    public boolean supportsMultipleDevices() {
        return maxDevices > 1;
    }

    public void updatePrice(BigDecimal newPrice) {
        setPrice(newPrice);
    }

    // Object Methods
    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", price=" + price +
                ", durationDays=" + durationDays +
                ", maxDevices=" + maxDevices +
                ", planDescription='" + planDescription + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SubscriptionPlan)) {
            return false;
        }

        SubscriptionPlan plan = (SubscriptionPlan) obj;

        return planId != null
                && planId.equals(plan.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(planId);
    }
}
