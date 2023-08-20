package com.fortress.security.controller;

import com.fortress.security.dto.UserDTO;
import com.fortress.security.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/auth")

public class UserControllerAuth {
    private final UserService userService;

    public UserControllerAuth(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getNameOfAuthenticatedUser")
    UserDTO getNameOfAuthenticatedUser(){
        return userService.getNameOfAuthenticatedUser();
    }

    @PostMapping("/changePassword")
    void changePassword(@RequestBody UserDTO userDTO){
        userService.changePassword(userDTO);
    }

    @PostMapping("/changeName")
    void changeName(@RequestBody UserDTO userDTO){
        userService.changeName(userDTO);
    }
}
