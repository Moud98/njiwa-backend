package com.wssoftwares.ikutibnb_backend.sms;

import java.util.List;
import com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage;

public interface SmsService {
    /**
     * Send SMS to a single phone number
     */
    void sendSms(String phoneNumber, String message);

    /**
     * Send SMS to multiple phone numbers with same text
     */
    void sendSmsToMultiple(List<String> phoneNumbers, String message);

    /**
     * Send multiple SMS messages, each with own destination and text
     */
    void sendSmsMessages(List<SmsMessage> messages);
}
