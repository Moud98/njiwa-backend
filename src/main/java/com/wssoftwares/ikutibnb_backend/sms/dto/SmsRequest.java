package com.wssoftwares.ikutibnb_backend.sms.dto;

public class SmsRequest {
    private String phonenumber;
    private String message;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
