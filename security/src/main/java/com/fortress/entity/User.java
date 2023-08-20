package com.fortress.entity;

import com.fortress.dto.UserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Document("users")
@Validated
public class User implements UserDetails, EntityInterface{

    @Id
    private String id;
    @NotNull
    private String username;
    @NotNull
    private String name;
    @NotNull
    private String password;
    private String passwordResetCode;
    @NotNull
    private Role role;

    private boolean accountNonLocked = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserDTO getDTO(){
        var dto = new UserDTO();
        dto.setId(id);
        return dto;
    }
}
