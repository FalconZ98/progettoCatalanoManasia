package com.catalanomanasia.project.service;

import com.catalanomanasia.project.model.Role;
import com.catalanomanasia.project.model.Store;
import com.catalanomanasia.project.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private User user;

    public User getUser(){
        return this.user;
    }

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        Set<Role> roles = new HashSet<>();
        roles.add(user.getRole());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for(Role role : roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
        return user.getEnabled();
    }

    public String getFullName() {
        return user.getFirstName() + " " + user.getLastName();
    }

    public Store getStore(){
        return this.user.getStore();
    }
}
