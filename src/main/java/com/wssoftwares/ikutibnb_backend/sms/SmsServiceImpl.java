package com.wssoftwares.ikutibnb_backend.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.wssoftwares.ikutibnb_backend.sms.dto.SmsMessage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${sms.provider:mock}")
    private String smsProvider;

    @Value("${sms.account-sid:}")
    private String accountSid;

    @Value("${sms.auth-token:}")
    private String authToken;

    @Value("${sms.from-number:+255747978832}")
    private String fromNumber;

    @Value("${nextsms.api-key:}")
    private String nextSmsApiKey;

    @Value("${nextsms.api-secret:}")
    private String nextSmsApiSecret;

    @Value("${nextsms.endpoint:https://messaging-service.co.tz/api/sms/v1/test/text/single}")
    private String nextSmsEndpoint;

    @Value("${nextsms.endpoint-multi:https://messaging-service.co.tz/api/sms/v1/text/multi}")
    private String nextSmsEndpointMulti;

    @Value("${nextsms.from-name:NJIWA APP}")
    private String nextSmsFromName;

    /**
     * Send SMS to a single phone number
     */
    @Override
    public void sendSms(String phonenumber, String message) {
        List<String> phonenumbers = List.of(phonenumber);
        sendSmsToMultiple(phonenumbers, message);
    }

    /**
     * Send SMS to multiple phone numbers
     */
    @Override
    public void sendSmsToMultiple(List<String> phonenumbers, String message) {
        if (phonenumbers == null || phonenumbers.isEmpty()) {
            logger.warn("No phone numbers provided for SMS");
            return;
        }

        switch (smsProvider.toLowerCase()) {
            case "twilio":
                sendViaTwitter(phonenumbers, message);
                break;
            case "aws_sns":
                sendViaAwsSns(phonenumbers, message);
                break;
            case "nextsms":
                sendViaNextSms(phonenumbers, message);
                break;
            case "mock":
            default:
                sendViaMock(phonenumbers, message);
                break;
        }
    }

    @Override
    public void sendSmsMessages(List<SmsMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            logger.warn("No message objects provided for SMS");
            return;
        }

        switch (smsProvider.toLowerCase()) {
            case "twilio":
            case "aws_sns":
                // For providers that don’t support built-in multi-message objects,
                // send each message one-by-one
                for (SmsMessage message : messages) {
                    if (message.getTo() == null || message.getText() == null) {
                        continue;
                    }
                    sendSms(message.getTo(), message.getText());
                }
                break;
            case "nextsms":
                sendViaNextSmsMulti(messages);
                break;
            case "mock":
            default:
                for (SmsMessage message : messages) {
                    if (message.getTo() == null || message.getText() == null) {
                        continue;
                    }
                    logger.info("MOCK SMS - From: {}, To: {}, Message: {}", message.getFrom(), message.getTo(), message.getText());
                }
                break;
        }
    }

    /**
     * Mock implementation for testing/development
     */
    private void sendViaMock(List<String> phonenumbers, String message) {
        for (String phonenumber : phonenumbers) {
            logger.info("MOCK SMS - To: {}, Message: {}", phonenumber, message);
        }
    }

    /**
     * Twilio implementation
     * Add twilio-java dependency to pom.xml to use this
     */
    private void sendViaTwitter(List<String> phonenumbers, String message) {
        try {
            // Import the Twilio SDK classes when adding Twilio dependency
            // Example below shows how it would be implemented:
            /*
            import com.twilio.Twilio;
            import com.twilio.rest.api.v2010.account.Message;
            import com.twilio.type.phonenumber;

            for (String phonenumber : phonenumbers) {
                Message msg = Message.creator(
                    new phonenumber(phonenumber),      // To number
                    new phonenumber(fromNumber),       // From number
                    message                            // Message body
                ).create();
                logger.info("SMS sent via Twilio - SID: {}", msg.getSid());
            }
            */

            // Fallback to mock if Twilio is not configured
            logger.warn("Twilio SMS provider not properly configured. Using mock implementation.");
            sendViaMock(phonenumbers, message);

        } catch (Exception e) {
            logger.error("Error sending SMS via Twilio", e);
            throw new RuntimeException("Failed to send SMS via Twilio", e);
        }
    }

    private void sendViaAwsSns(List<String> phonenumbers, String message) {
        try {

            // Fallback to mock if AWS SNS is not configured
            logger.warn("AWS SNS provider not properly configured. Using mock implementation.");
            sendViaMock(phonenumbers, message);

        } catch (Exception e) {
            logger.error("Error sending SMS via AWS SNS", e);
            throw new RuntimeException("Failed to send SMS via AWS SNS", e);
        }
    }

    
    private void sendViaNextSms(List<String> phonenumbers, String message) {
        try {
            if (nextSmsApiKey == null || nextSmsApiKey.isEmpty() || 
                nextSmsApiSecret == null || nextSmsApiSecret.isEmpty()) {
                logger.warn("NextSMS credentials not configured. Using mock implementation.");
                sendViaMock(phonenumbers, message);
                return;
            }

            try {
                // Prepare request payload according to NextSMS API format
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("from", nextSmsFromName);
                requestBody.put("to", phonenumbers);  // Array of phone numbers
                requestBody.put("text", message);
                requestBody.put("reference", generateReference());  // Optional reference

                // Prepare Basic Auth header (Base64 encode username:password)
                String auth = nextSmsApiKey + ":" + nextSmsApiSecret;
                String encodedAuth = java.util.Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                logger.debug("NextSMS Configuration - Username: {}, Endpoint: {}", nextSmsApiKey, nextSmsEndpoint);
                logger.debug("NextSMS Auth Header: Basic {}", encodedAuth);

                // Prepare headers according to NextSMS API requirements
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", "application/json");
                headers.set("Authorization", "Basic " + encodedAuth);

                // Create HTTP entity
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                // Send request to NextSMS API
                logger.info("Sending SMS via NextSMS to {} recipients with endpoint: {}", 
                    phonenumbers.size(), nextSmsEndpoint);
                logger.debug("Request body: {}", requestBody);

                ResponseEntity<String> response = restTemplate.postForEntity(
                    nextSmsEndpoint,
                    entity,
                    String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    // Parse response to check for rejection errors
                    String responseBody = response.getBody();
                    logger.debug("NextSMS Response: {}", responseBody);
                    
                    // Check if response contains rejection errors
                    boolean hasRejectedSource = checkForRejectedSource(responseBody);
                    
                    if (hasRejectedSource) {
                        logger.error("✗ NextSMS Sender ID Registration Error");
                        logger.error("  Sender ID '{}' is NOT registered on your NextSMS account", nextSmsFromName);
                        logger.error("  Action Required: Register '{}' as a sender ID in your NextSMS account", nextSmsFromName);
                        logger.error("  Visit: https://app.nextsms.co.tz to manage your sender IDs");
                        logger.warn("  Falling back to mock SMS implementation for now");
                        sendViaMock(phonenumbers, message);
                    } else {
                        logger.info("✓ SMS sent via NextSMS to {} recipients - Response: {}", 
                            phonenumbers.size(), responseBody);
                    }
                } else {
                    logger.warn("✗ NextSMS returned non-2xx status: {} - Response: {}", 
                        response.getStatusCode(), response.getBody());
                    throw new RuntimeException("NextSMS API returned status: " + response.getStatusCode());
                }

            } catch (org.springframework.web.client.HttpClientErrorException.Forbidden e) {
                logger.error("✗ NextSMS Authentication Failed (403 Forbidden). Check your credentials.");
                logger.error("  API Key: {}", nextSmsApiKey);
                logger.error("  API Secret: {}***", nextSmsApiSecret.substring(0, Math.min(3, nextSmsApiSecret.length())));
                logger.error("  Error Response: {}", e.getResponseBodyAsString());
                throw new RuntimeException("Failed to send SMS via NextSMS: Not Authorized (403)", e);
                
            } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
                logger.error("✗ NextSMS Authentication Failed (401 Unauthorized). Check your credentials.");
                logger.error("  Error Response: {}", e.getResponseBodyAsString());
                throw new RuntimeException("Failed to send SMS via NextSMS: Unauthorized (401)", e);
                
            } catch (Exception e) {
                logger.error("✗ Error sending SMS via NextSMS to phone numbers: {}", phonenumbers, e);
                throw new RuntimeException("Failed to send SMS via NextSMS: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            logger.error("✗ Error in NextSMS SMS sending process", e);
            throw new RuntimeException("Failed to send SMS via NextSMS", e);
        }
    }

    private void sendViaNextSmsMulti(List<SmsMessage> messages) {
        if (nextSmsApiKey == null || nextSmsApiKey.isEmpty() || 
            nextSmsApiSecret == null || nextSmsApiSecret.isEmpty()) {
            logger.warn("NextSMS credentials not configured. Using mock implementation.");
            for (SmsMessage message : messages) {
                logger.info("MOCK SMS - From: {}, To: {}, Message: {}", message.getFrom(), message.getTo(), message.getText());
            }
            return;
        }

        try {
            // Build the message objects expected by the multi endpoint
            List<Map<String, String>> messageObjects = new java.util.ArrayList<>();
            for (SmsMessage message : messages) {
                if (message.getTo() == null || message.getText() == null) {
                    continue;
                }
                String fromName = resolveFromName(message.getFrom());
                Map<String, String> msg = new HashMap<>();
                msg.put("from", fromName);
                msg.put("to", message.getTo());
                msg.put("text", message.getText());
                msg.put("reference", generateReference()); // unique per message to avoid duplicate rejection
                messageObjects.add(msg);
            }

            if (messageObjects.isEmpty()) {
                logger.warn("No valid message entries for NextSMS multi send");
                return;
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", messageObjects);
            requestBody.put("reference", generateReference());

            String auth = nextSmsApiKey + ":" + nextSmsApiSecret;
            String encodedAuth = java.util.Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            logger.info("Sending SMS via NextSMS multi endpoint with {} messages, endpoint: {}", messageObjects.size(), nextSmsEndpointMulti);
            logger.debug("NextSMS multi request body: {}", requestBody);

            ResponseEntity<String> response = restTemplate.postForEntity(
                nextSmsEndpointMulti,
                entity,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                logger.debug("NextSMS multi Response: {}", responseBody);

                boolean hasRejectedSource = checkForRejectedSource(responseBody);
                if (hasRejectedSource) {
                    java.util.Set<String> fromNamesUsed = new java.util.HashSet<>();
                    for (Map<String, String> msg : messageObjects) {
                        fromNamesUsed.add(msg.getOrDefault("from", ""));
                    }
                    logger.error("✗ NextSMS Sender ID Registration Error");
                    logger.error("  Sender ID(s) used in this request: {}", fromNamesUsed);
                    logger.error("  Default configured sender ID: '{}'", resolveFromName(null));
                    logger.error("  Action Required: Ensure each sender ID above is approved/active in your NextSMS account");
                    logger.error("  Visit: https://app.nextsms.co.tz to manage your sender IDs");
                    logger.warn("  No SMS fallback applied for this multi request");
                } else {
                    logger.info("✓ SMS sent via NextSMS multi endpoint for {} message(s) - Response: {}", messageObjects.size(), responseBody);
                }
            } else {
                logger.warn("✗ NextSMS multi returned non-2xx status: {} - Response: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("NextSMS API returned status: " + response.getStatusCode());
            }

        } catch (org.springframework.web.client.HttpClientErrorException.Forbidden e) {
            logger.error("✗ NextSMS Authentication Failed (403 Forbidden). Check your credentials.");
            logger.error("  API Key: {}", nextSmsApiKey);
            logger.error("  API Secret: {}***", nextSmsApiSecret.substring(0, Math.min(3, nextSmsApiSecret.length())));
            logger.error("  Error Response: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to send SMS via NextSMS multi: Not Authorized (403)", e);
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            logger.error("✗ NextSMS Authentication Failed (401 Unauthorized). Check your credentials.");
            logger.error("  Error Response: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to send SMS via NextSMS multi: Unauthorized (401)", e);
        } catch (org.springframework.web.client.HttpClientErrorException.UnprocessableEntity e) {
            // NextSMS may reject exact duplicate messages sent within 24 hours
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && responseBody.contains("Duplicate request sent within past 24 hour")) {
                logger.warn("⚠️ Duplicate NextSMS multi request detected; treating as non-fatal: {}", responseBody);
                return;
            }
            logger.error("✗ NextSMS multi request invalid (422 Unprocessable Entity): {}", responseBody);
            throw new RuntimeException("Failed to send SMS via NextSMS multi: Unprocessable Entity (422)", e);
        } catch (Exception e) {
            logger.error("✗ Error sending SMS via NextSMS multi to phone numbers", e);
            throw new RuntimeException("Failed to send SMS via NextSMS multi: " + e.getMessage(), e);
        }
    }

    /**
     * Check if NextSMS response contains REJECTED_SOURCE error
     */
    private boolean checkForRejectedSource(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode messages = root.get("messages");
            
            if (messages != null && messages.isArray()) {
                for (JsonNode message : messages) {
                    JsonNode status = message.get("status");
                    if (status != null) {
                        String statusName = status.get("name").asText("");
                        if ("REJECTED_SOURCE".equals(statusName)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not parse NextSMS response", e);
        }
        return false;
    }

    /**
     * Generate a unique reference for NextSMS API request
     */
    private String generateReference() {
        return "REF-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String resolveFromName(String requestedFrom) {
        if (requestedFrom != null && !requestedFrom.trim().isEmpty()) {
            return requestedFrom.trim();
        }
        if (nextSmsFromName != null && !nextSmsFromName.trim().isEmpty()) {
            return nextSmsFromName.trim();
        }
        return "N-SMS";
    }
}