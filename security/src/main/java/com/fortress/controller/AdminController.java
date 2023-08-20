package com.fortress.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/dashboard")
    String getAdminDashboard() {
        return "admindashboard";
    }

    @GetMapping("/login")
    ModelAndView login(){
        return new ModelAndView("login",
                "redirect",
                "dashboard");
    }
}
