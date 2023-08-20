package com.fortress.security.controller;

import com.fortress.security.dto.UserDTO;
import com.fortress.security.entity.User;
import com.fortress.security.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/admin")
public class UserControllerAdmin extends CRUDController<User, UserDTO> {
    private final UserService userService;

    public UserControllerAdmin(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @GetMapping("/findByUsername/{username}")
    UserDTO findByUsername(@PathVariable String username){
        return userService.findDTOByUsername(username);
    }

    @GetMapping({"/findUsersStartsWith", "/findUsersStartsWith/{usernamePrefix}"})
    List<UserDTO> findUsersStartsWith(@PathVariable(required = false) String usernamePrefix){
        return userService.findUsersStartsWith(usernamePrefix);
    }

    @PostMapping("/emailTempPassword")
    void emailTempPassword(@RequestBody UserDTO userDTO){
        userService.emailSecurePassword(userDTO);
    }

    @PostMapping("/toggleLock")
    void toggleLock(@RequestBody UserDTO userDTO){
        userService.toggleLock(userDTO);
    }

    @PostMapping("/addUser")
    synchronized void add(@RequestBody UserDTO dto) {
        super.add(dto);
    }
}
