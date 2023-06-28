package com.catalanomanasia.project.service;

import com.catalanomanasia.project.model.User;
import com.catalanomanasia.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, LockedException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Utente non trovato!");
        }
        if((!user.getEnabled()) && user.getRole().getName().equals("ROLE_MERCHANT")){
            throw new LockedException("Utente non abilitato");
        }
        return new CustomUserDetails(user);
    }
}
