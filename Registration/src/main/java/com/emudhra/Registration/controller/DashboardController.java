package com.emudhra.Registration.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.emudhra.Registration.constants.SessionAttribute;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboardPage(HttpSession session) {

            if(session.getAttribute(SessionAttribute.USER)==null) {
                return "redirect:/login";
            }
        return "dashboard";
    }
}