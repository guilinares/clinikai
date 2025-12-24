package com.guilinares.clinicai.security;

import com.guilinares.clinicai.user.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {

    @Getter
    private final UUID id;
    @Getter
    private final String name;
    private final String email;
    private final String password;
    private final String role;
    @Getter
    private final UUID clinicId;
    @Getter
    private final String clinicName;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UserEntity user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.role = user.getRole();
        this.clinicId = user.getClinic() != null ? user.getClinic().getId() : null;
        this.clinicName = user.getClinic() != null ? user.getClinic().getName() : null;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // ==== getters extras para o /api/me ====

    public String getRoleName() {
        return role;
    }

    // ==== implementações de UserDetails ====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // username = email
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
