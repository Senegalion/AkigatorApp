package org.example.akigatorapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/error-access")
    public String accessDeniedPage(Model model) {
        model.addAttribute("message", "You do not have permission to access this page.");
        return "error-access";
    }
}
