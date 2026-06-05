package com.emudhra.Registration.controller;

import com.emudhra.Registration.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SMTPController {
    @Autowired
    EmailService emailService;
    @GetMapping("/test-mail")
    @ResponseBody
    public String testMail() {

        emailService.sendOtp(
                "your-email@gmail.com",
                "123456"
        );
        return "Mail Sent";
    }
}
