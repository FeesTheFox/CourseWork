package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String login() {
        logger.info("Handling login request");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        logger.info("Handling register request");
        return "register";
    }
}