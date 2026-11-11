package com.wssoftwares.ikutibnb_backend.sms.dto;

import java.util.List;

public class SmsMultiMessagesRequest {
    private List<SmsMessage> messages;
    private String reference;

    public List<SmsMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<SmsMessage> messages) {
        this.messages = messages;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}