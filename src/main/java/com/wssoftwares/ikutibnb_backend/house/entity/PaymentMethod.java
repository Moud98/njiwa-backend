package com.wssoftwares.ikutibnb_backend.house.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PaymentMethod {

    private String method;
    private String details;

    public PaymentMethod() {
    }

    public PaymentMethod(String method, String details) {
        this.method = method;
        this.details = details;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PaymentMethod))
            return false;
        PaymentMethod that = (PaymentMethod) o;
        return Objects.equals(method, that.method)
                && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, details);
    }
}
