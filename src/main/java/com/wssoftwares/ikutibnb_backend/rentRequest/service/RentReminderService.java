package com.wssoftwares.ikutibnb_backend.rentRequest.service;

import com.wssoftwares.ikutibnb_backend.rentRequest.RentRequestRepository;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling automatic rent reminders
 * Sends SMS reminders to house owners and clients when rent days remaining <= 15
 * Resets the reminder flag after 24 hours
 */
@Service
public class RentReminderService {

    private static final Logger logger = LoggerFactory.getLogger(RentReminderService.class);
    private static final long REMINDER_THRESHOLD_DAYS = 15; // Threshold for sending reminders
    private static final long RESET_AFTER_HOURS = 24; // Reset reminder flag after 24 hours

    @Autowired
    private RentRequestRepository rentRequestRepository;

    @Autowired
    private SmsService smsService;

    /**
     * Check all rent requests and send reminders for those with <= 15 days remaining
     * This method should be called every hour by a scheduled task
     */
    @Transactional
    public void checkAndSendReminders() {
        logger.info("Starting reminder check for rent requests");

        try {
            // Find all rent requests that need reminders
            List<RentRequest> rentRequestsNeedingReminders = rentRequestRepository.findRentRequestsNeedingReminders();

            if (rentRequestsNeedingReminders.isEmpty()) {
                logger.info("No rent requests found that need reminders");
                return;
            }

            logger.info("Found {} rent requests needing reminders", rentRequestsNeedingReminders.size());

            for (RentRequest rentRequest : rentRequestsNeedingReminders) {
                sendReminderForRentRequest(rentRequest);
            }

            logger.info("Completed reminder check and sending");

        } catch (Exception e) {
            logger.error("Error checking and sending reminders", e);
        }
    }

    /**
     * Send reminder SMS to both house owner and client for a specific rent request
     */
    private void sendReminderForRentRequest(RentRequest rentRequest) {
        try {
            String clientName = rentRequest.getClient().getFullname() != null ? 
                rentRequest.getClient().getFullname() : "Valued Client";
            String ownerName = rentRequest.getHouseOwner().getFullname() != null ? 
                rentRequest.getHouseOwner().getFullname() : "House Owner";
            String houseName = rentRequest.getRoom().getHouse().getHouseName() != null ?
                rentRequest.getRoom().getHouse().getHouseName() : "Your House";
            Long daysRemaining = rentRequest.getTimer();

            // Prepare phone numbers
            List<String> phoneNumbers = new ArrayList<>();
            
            String clientPhone = rentRequest.getClient().getPhonenumber();
            if (clientPhone != null && !clientPhone.isEmpty()) {
                phoneNumbers.add(clientPhone);
            }

            String ownerPhone = rentRequest.getHouseOwner().getPhonenumber();
            if (ownerPhone != null && !ownerPhone.isEmpty()) {
                phoneNumbers.add(ownerPhone);
            }

            // Skip if no valid phone numbers
            if (phoneNumbers.isEmpty()) {
                logger.warn("No valid phone numbers for rent request ID: {}", rentRequest.getId());
                return;
            }

            // Send separate SMS to client and house owner
            logger.info("Sending reminder SMS for rent request ID: {} with separate messages", rentRequest.getId());

            List<com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage> messages = new ArrayList<>();

            if (clientPhone != null && !clientPhone.isEmpty()) {
                com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage clientSms = new com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage();
                clientSms.setTo(clientPhone);
                clientSms.setText(String.format(
                                "Mpendwa %s,\n\n" +
                                "Tunapenda kukukumbusha kuwa mkataba wako wa chumba %s umebakisha siku %d.\n\n" +
                                "Tafadhali tembelea Njiwa App:\n" +
                                "https://njiwa.worldspringsoftwares.com\n" +
                                "ili kuongeza au kuboresha mkataba wako kwa urahisi.\n\n" +
                                "Asante kwa kutumia huduma zetu.",
                clientName, houseName, daysRemaining));
                messages.add(clientSms);
            }

            if (ownerPhone != null && !ownerPhone.isEmpty()) {
                com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage ownerSms = new com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage();
                ownerSms.setTo(ownerPhone);
                ownerSms.setText(String.format(
                                "Mpendwa %s,\n\n" +
                                "Tunakujulisha kuwa mpangaji wako %s wa chumba %s amebakiza siku %d kwenye mkataba wake.\n\n" +
                                "Tafadhali tembelea Njiwa App:\n" +
                                "https://njiwa.worldspringsoftwares.com\n" +
                                "ili kuchukua hatua stahiki kwa wakati na kuhakikisha muendelezo mzuri wa upangishaji.\n\n" +
                                "Asante kwa kutumia huduma zetu.",
                ownerName, clientName, houseName, daysRemaining));
                messages.add(ownerSms);
            }

            if (!messages.isEmpty()) {
                smsService.sendSmsMessages(messages);
            } else {
                logger.warn("No valid phone numbers found for rent request ID: {}", rentRequest.getId());
                return;
            }

            // Update the reminder sent flag and timestamp
            rentRequest.setReminderSent(true);
            rentRequest.setReminderSentTimestamp(LocalDateTime.now());
            rentRequestRepository.save(rentRequest);

            logger.info("Successfully sent reminders and updated rent request ID: {}", rentRequest.getId());

        } catch (Exception e) {
            logger.error("Error sending reminder for rent request ID: {}", rentRequest.getId(), e);
        }
    }

    /**
     * Reset the reminder flag for rent requests that haven't been reset in 24 hours
     * This method should be called by a scheduled task (every hour is fine)
     */
    public void resetRemindersOlderThan24Hours() {
        logger.info("Starting reminder reset check");

        try {
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(RESET_AFTER_HOURS);
            List<RentRequest> rentRequestsToReset = 
                rentRequestRepository.findRentRequestsToResetReminder(twentyFourHoursAgo);

            if (rentRequestsToReset.isEmpty()) {
                logger.info("No reminders found that need resetting");
                return;
            }

            logger.info("Resetting reminder flag for {} rent requests older than 24 hours", 
                rentRequestsToReset.size());

            for (RentRequest rentRequest : rentRequestsToReset) {
                try {
                    rentRequest.setReminderSent(false);
                    rentRequest.setReminderSentTimestamp(null);
                    rentRequestRepository.save(rentRequest);
                    
                    logger.debug("Reset reminder flag for rent request ID: {}", rentRequest.getId());
                } catch (Exception e) {
                    logger.error("Error resetting reminder for rent request ID: {}", 
                        rentRequest.getId(), e);
                }
            }

            logger.info("Completed reminder reset check");

        } catch (Exception e) {
            logger.error("Error during reminder reset check", e);
        }
    }
}
