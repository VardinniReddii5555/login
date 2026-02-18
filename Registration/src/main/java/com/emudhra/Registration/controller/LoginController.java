package com.emudhra.Registration.controller;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.emudhra.Registration.repository.UserRepository.passwordEncoder;

@Controller
public class LoginController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;


    @GetMapping({"","/","/login"})
    public String showLoginPage()
    {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {

        Users user = userRepository.findByEmail(username);

        // 1️⃣ Username not found
        if (user == null) {
            model.addAttribute("toastType", "error");
            model.addAttribute("toastMessage", "User not registered. Please register.");
            return "login";
        }

        // 2️⃣ Password mismatch
        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("toastType", "error");
            model.addAttribute("toastMessage", "Wrong password entered.");
            return "login";
        }

        // 3️⃣ Success
//        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }
}
