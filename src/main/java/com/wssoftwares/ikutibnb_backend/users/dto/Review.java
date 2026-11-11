package com.wssoftwares.ikutibnb_backend.users.dto;

public class Review {
    private String name;
    private long rate;
    private String message;

    public Review() {
    }

    public Review(String name, long rate, String message) {
        this.name = name;
        this.rate = rate;
        this.message = message;
    }

    public Review withName(String reviewerName) {
        return new Review(reviewerName, rate, message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
