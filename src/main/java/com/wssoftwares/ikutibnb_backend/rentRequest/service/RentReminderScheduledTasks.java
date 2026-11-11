package com.wssoftwares.ikutibnb_backend.rentRequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduled task configuration for rent reminders
 * 
 * Runs the following tasks:
 * 1. Check and send reminders every hour (fixedDelay: 60 minutes)
 * 2. Reset reminders older than 24 hours every hour
 * 
 * To disable scheduling, set spring.task.scheduling.enabled=false in application.properties
 */
@Configuration
@EnableScheduling
public class RentReminderScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(RentReminderScheduledTasks.class);

    @Autowired
    private RentReminderService rentReminderService;

    /**
     * Check and send rent reminders every hour (3600000 milliseconds)
     * The first execution will happen after 1 minute (initialDelay) to allow the app to fully start
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 180000)
    public void checkAndSendRemindersScheduled() {
        logger.info("Executing scheduled task: checkAndSendReminders");
        try {
            rentReminderService.checkAndSendReminders();
        } catch (Exception e) {
            logger.error("Scheduled task error in checkAndSendReminders", e);
        }
    }

    /**
     * Reset reminders older than 24 hours every hour
     * The first execution will happen after 2 minutes to allow the app to fully start
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 240000)
    public void resetRemindersOlderThan24HoursScheduled() {
        logger.info("Executing scheduled task: resetRemindersOlderThan24Hours");
        try {
            rentReminderService.resetRemindersOlderThan24Hours();
        } catch (Exception e) {
            logger.error("Scheduled task error in resetRemindersOlderThan24Hours", e);
        }
    }
}
