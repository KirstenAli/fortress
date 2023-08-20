package com.fortress.controller;

import com.fortress.dto.UserDTO;
import com.fortress.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/public")
public class UserControllerPublic {
    private final UserService userService;

    public UserControllerPublic(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sendPasswordResetToken")
    void sendPasswordResetToken(@RequestBody UserDTO userDTO){
        userService.sendPasswordResetToken(userDTO);
    }

    @PostMapping("/resetPasswordWithToken")
    void resetPasswordWithToken(@RequestBody UserDTO userDTO){
        userService.resetPasswordWithToken(userDTO);
    }
}
