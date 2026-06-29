package com.emudhra.Registration.controller;

import com.emudhra.Registration.model.PushRequest;
import com.emudhra.Registration.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
public class PushNotificationController {

    @Autowired
    private PushNotificationService pushNotificationService;

    // Portal creates login request
    @PostMapping("/send")
    public String send(@RequestParam String email) {

        return pushNotificationService.createLoginRequest(email);
    }

    // Android polls for pending request
    @GetMapping("/pending")
    public PushRequest pending(@RequestParam String email) {

        return pushNotificationService.getPendingRequest(email);
    }

    // Android approves request
    @PostMapping("/approve")
    public String approve(@RequestParam String requestId) {

        boolean approved =
                pushNotificationService.approveRequest(requestId);

        return approved ? "APPROVED" : "NOT_FOUND";
    }

    // Android rejects request
    @PostMapping("/reject")
    public String reject(@RequestParam String requestId) {

        boolean rejected =
                pushNotificationService.rejectRequest(requestId);

        return rejected ? "REJECTED" : "NOT_FOUND";
    }

    // Portal checks status
    @GetMapping("/status")
    public String status(@RequestParam String requestId) {

        return pushNotificationService.getRequestStatus(requestId);
    }

}