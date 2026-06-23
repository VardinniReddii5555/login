package com.emudhra.Registration.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.JwtTokenResponse;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.repository.UserRepository;
//import com.emudhra.Registration.service.JwtAuthService;
import com.emudhra.Registration.service.KeyclockJwtService;
import jakarta.servlet.http.HttpSession;
import com.emudhra.Registration.constants.SessionAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class JwtLoginController {

//    @Autowired
//    private JwtAuthService jwtAuthService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public static final KeyclockJwtService keycloakJwtService = new KeyclockJwtService();

    @GetMapping("/jwt-login")
    public String showJwtLoginPage() {

        return "jwt-login";
    }

    @PostMapping("/jwt-authenticate")
    public String authenticate(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        try {

            JwtTokenResponse response = keycloakJwtService.authenticate(username, password);

            String accessToken = response.getAccessToken();

            DecodedJWT jwt = JWT.decode(accessToken);

            String preferredUsername = jwt.getClaim("preferred_username").asString();

            String email = jwt.getClaim("email").asString();

            String name = jwt.getClaim("name").asString();

            Users user = userRepository.findByEmail(email);

            if (user == null) {

                user = new Users();

                user.setUsername(preferredUsername);
                user.setEmail(email);
                user.setRegistration_mode(LoginModes.KEYCLOCK_JWT);

                user = userRepository.save(user);
            }
            LoginAudit loginAudit = new LoginAudit();
            loginAudit.setUser(user);
            loginAudit.setSessionId(session.getId());
            loginAudit.setLoginMode(LoginModes.KEYCLOCK_JWT);
            loginAudit.setLoginAt(LocalDateTime.now());
            loginAuditRepository.save(loginAudit);
            session.setAttribute(SessionAttribute.USER, user);


            System.out.println("JWT LOGIN SUCCESS");
            System.out.println("Username = " + preferredUsername);
            System.out.println("Email = " + email);

            session.setAttribute("jwtToken", accessToken);
            session.setAttribute("username", preferredUsername);
            session.setAttribute("email", email);
            session.setAttribute("name", name);

            return "redirect:/dashboard";

        }
        catch (Exception ex) {

            ex.printStackTrace();

            model.addAttribute(
                    "error",
                    "Invalid Username or Password");

            return "jwt-login";
        }
    }
}