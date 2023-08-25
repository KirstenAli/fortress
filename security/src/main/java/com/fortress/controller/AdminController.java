package com.fortress.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController extends FortressController{
    @GetMapping("/view")
    String adminDashboard() {
        return "admindashboard";
    }
}
