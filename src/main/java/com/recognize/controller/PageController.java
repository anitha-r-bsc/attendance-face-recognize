package com.recognize.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/register-page")
    public String registerPage() {
        return "register"; // register.html
    }

    @GetMapping("/mark-page")
    public String markPage() {
        return "mark"; // mark.html
    }

    @GetMapping("/")
    public String students() {
        return "students"; // mark.html
    }
}
