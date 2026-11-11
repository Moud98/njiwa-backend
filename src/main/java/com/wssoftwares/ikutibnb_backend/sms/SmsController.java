package com.wssoftwares.ikutibnb_backend.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wssoftwares.ikutibnb_backend.sms.dto.SmsRequest;
import com.wssoftwares.ikutibnb_backend.sms.dto.SmsMultipleRequest;
import com.wssoftwares.ikutibnb_backend.sms.dto.SmsMultiMessagesRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<?> sendSms(@RequestBody SmsRequest request) {
        try {
            smsService.sendSms(request.getPhonenumber(), request.getMessage());
            return ResponseEntity.ok(Map.of("status", "success", "message", "SMS sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/send-multiple")
    public ResponseEntity<?> sendSmsToMultiple(@RequestBody SmsMultipleRequest request) {
        try {
            smsService.sendSmsToMultiple(request.getPhoneNumbers(), request.getMessage());
            return ResponseEntity.ok(Map.of("status", "success", "message", "SMS sent to all recipients"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/send-multi-messages")
    public ResponseEntity<?> sendSmsMessages(@RequestBody SmsMultiMessagesRequest request) {
        try {
            smsService.sendSmsMessages(request.getMessages());
            return ResponseEntity.ok(Map.of("status", "success", "message", "Multiple SMS messages sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}