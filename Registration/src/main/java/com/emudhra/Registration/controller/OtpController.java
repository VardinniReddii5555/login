package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.UserOtp;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.repository.UserOtpRepository;
import com.emudhra.Registration.repository.UserRepository;
import com.emudhra.Registration.service.EmailService;
import com.emudhra.Registration.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class OtpController {

    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private UserOtpRepository otpRepository;

    @GetMapping("/email")
    public String emailPage(HttpSession session) {
        session.setAttribute("REQUIRE_OTP", true);
        return "email";
    }

    @PostMapping("/email")
    public String sendOtp(
            @RequestParam String email,
            HttpSession session) {

        Users user = userRepository.findByEmail(email);

        if(user == null) {
            session.setAttribute("error", "Email not registered");
            return "email";
        }

        UserOtp userOtp = otpService.createOtp(user.getUsername(), user.getEmail());
        session.setAttribute("EMAIL", email);
        emailService.sendOtp(email, userOtp.getOtp());

        return "otp";
    }

    @GetMapping("/otp")
    public String otpPage() {
        System.out.println("OTP PAGE HIT"); return "otp";}


    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("OTP PAGE HIT");

        String email = (String) session.getAttribute("EMAIL");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login";
        }

        UserOtp userOtp = otpRepository
                        .findTopByEmailOrderByCreatedAtDesc(email)
                        .orElse(null);

        if (userOtp == null) {
            redirectAttributes.addFlashAttribute("error", "OTP not found.");
            return "redirect:/login";
        }
        System.out.println("Entered OTP = [" + otp + "]");
        System.out.println("Stored OTP = [" + userOtp.getOtp() + "]");

        if (LocalDateTime.now().isAfter(userOtp.getExpiryAt())) {
            redirectAttributes.addFlashAttribute("error", "OTP expired. Please login again.");
            return "redirect:/login";
        }

        String enteredOtp = otp.trim();
        String storedOtp = userOtp.getOtp().trim();

        if (!enteredOtp.equals(storedOtp)) {
            int attempts = userOtp.getAttempts() + 1;
            userOtp.setAttempts(attempts);
            otpRepository.save(userOtp);
            if (attempts >= 2) {
                redirectAttributes.addFlashAttribute("error", "Attempts completed. Please login again.");
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("error", "Invalid OTP. Remaining Attempts: " + (2 - attempts));
            return "redirect:/otp";
        }

        Users user = userRepository.findByEmail(email);
        userOtp.setVerified(true);
        otpRepository.save(userOtp);

        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setLoginAt(LocalDateTime.now());
        audit.setLoginMode(LoginModes.SMTP_AUTH);
        audit.setSessionId(session.getId());
        loginAuditRepository.save(audit);
        System.out.println(
                "EMAIL OTP AUDIT SAVED"
        );

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/login";
        }
        System.out.println("Entered OTP = [" + otp + "]");
        System.out.println("Stored OTP = [" + userOtp.getOtp() + "]");
        session.setAttribute(SessionAttribute.USER, user);
        session.setAttribute("OTP_VERIFIED", true);
        System.out.println("loggedInUser => " + session.getAttribute("loggedInUser"));
        System.out.println("SessionAttribute.USER => " + session.getAttribute(SessionAttribute.USER));
        redirectAttributes.addFlashAttribute("success", "OTP verified successfully.");
        return "redirect:/dashboard";
    }
}
