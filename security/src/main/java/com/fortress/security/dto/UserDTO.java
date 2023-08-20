package com.fortress.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fortress.security.entity.Role;
import com.fortress.security.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserDTO implements DTOInterface {
    private String id;
    private String username;
    private String name;
    private String password;
    private String oldPassword;
    private String passwordResetCode;
    private Role role;

    private boolean accountNonLocked;

    public UserDTO(String username, String name, Role role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }

    @JsonIgnore
    public User getEntity(){
        return new User();
    }
}
