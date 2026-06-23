package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.model.UserMfa;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.repository.UserMfaRepository;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.repository.UserRepository;
import com.emudhra.Registration.service.UserService;
import com.emudhra.Registration.constants.SessionAttribute;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Controller
public class MfaController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMfaRepository userMfaRepository;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private UserRepository  userRepository;

    @GetMapping("/mfa-register")
    public String registerPage() {
        return "mfa-register";
    }

    @PostMapping("/mfa-register")
    public String generateQr(
            @RequestParam String username,
            HttpSession session,
            Model model) throws Exception {

        Optional<UserMfa> existing = userMfaRepository.findByUsername(username);

        if(existing.isPresent()) {
            model.addAttribute("error", "User already registered for MFA");
            return "mfa-register";
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();
        session.setAttribute("pendingUsername", username);
        session.setAttribute("pendingSecret", secret);

        String otpUrl =
                "otpauth://totp/SECUREPass:"
                        + username
                        + "?secret="
                        + secret
                        + "&issuer=SECUREPass";
        String qrCode = generateQRCode(otpUrl);
        model.addAttribute("qrCode", qrCode);
        model.addAttribute("username", username);

        return "QR";
    }
    @PostMapping("/verify-registration")
    public String verifyRegistration(
            @RequestParam String username,
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        String secret = (String) session.getAttribute("pendingSecret");
        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        boolean valid = gAuth.authorize(secret, Integer.parseInt(otp));

        if(!valid) {
            model.addAttribute("error", "Invalid OTP");
            model.addAttribute("username", username);
            return "QR";
        }

        Users user = userRepository.findByUsername(username);

        if(user == null) {
            String email = username + "@securepass.local";
            user = new Users();
            user.setUsername(username);
            user.setEmail(username + "@mfa.local");
            user.setPassword( passwordEncoder.encode("TEMP123"));
            user.setRegistration_mode(LoginModes.AUTHENTICATOR);
            userRepository.save(user);
        }

        UserMfa mfa = new UserMfa();
        mfa.setUsername(username);
        mfa.setSecretKey(secret);
        mfa.setMfaEnabled(true);
        mfa.setCreatedDate(LocalDateTime.now());

        userMfaRepository.save(mfa);

        LoginAudit audit = new LoginAudit();

        audit.setUser(user);
        audit.setSessionId(session.getId());
        audit.setLoginAt(LocalDateTime.now());
        audit.setLoginMode(LoginModes.AUTHENTICATOR);
        loginAuditRepository.save(audit);

        model.addAttribute("message", "MFA Registration Successful");
        return "authenticator-login";
    }

    @GetMapping("/authenticator-login")
    public String authenticatorLoginPage() {
        return "authenticator-login";
    }

    @PostMapping("/authenticator-login")
    public String authenticatorLogin(
            @RequestParam String username,
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        Users user = userRepository.findByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found");
            return "authenticator-login";
        }

        UserMfa mfa = userMfaRepository
                        .findByUsername(username)
                        .orElse(null);

        if (mfa == null) {
            model.addAttribute("error", "Not registered for MFA");
            return "authenticator-login";
        }

        if (!Boolean.TRUE.equals(mfa.getMfaEnabled())) {

            model.addAttribute("error", "MFA registration incomplete");
            return "authenticator-login";
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean valid = gAuth.authorize(mfa.getSecretKey(), Integer.parseInt(otp));

        if (!valid) {
            model.addAttribute("error", "Invalid OTP");
            return "authenticator-login";
        }

        session.setAttribute(SessionAttribute.USER, user);

        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setSessionId(session.getId());
        audit.setLoginAt(LocalDateTime.now());
        audit.setLoginMode(LoginModes.AUTHENTICATOR);

        loginAuditRepository.save(audit);

        return "redirect:/dashboard";
    }

    private String generateQRCode(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", output);
        return Base64.getEncoder().encodeToString(output.toByteArray());
    }
}