package com.emudhra.Registration.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import java.util.Enumeration;

@Controller
public class HeaderController {

    @GetMapping("/headers")
    @ResponseBody
    public String headers(HttpServletRequest request) {

        Enumeration<String> names = request.getHeaderNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            System.out.println(name + " = " + request.getHeader(name));
        }

        return "Headers printed in console";
    }
}
