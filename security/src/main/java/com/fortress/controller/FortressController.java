package com.fortress.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

public abstract class FortressController {

    @GetMapping("/login")
    ModelAndView login(){
        return new ModelAndView("login",
                "redirect",
                "view");
    }
}
