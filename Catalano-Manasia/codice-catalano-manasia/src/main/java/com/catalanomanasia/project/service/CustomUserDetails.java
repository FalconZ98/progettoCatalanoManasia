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


    /*La classe CustomUserDetails sembra essere una classe personalizzata che implementa l'interfaccia UserDetails 
    di Spring Security. Questa classe viene utilizzata per fornire informazioni dettagliate sull'utente al sistema 
    di autenticazione di Spring Security.
La classe CustomUserDetails ha un campo user di tipo User, che rappresenta l'oggetto utente associato a 
    questa istanza di CustomUserDetails. Il metodo getUser() restituisce l'oggetto User associato.
Il costruttore CustomUserDetails(User user) viene utilizzato per creare un'istanza di CustomUserDetails a 
    partire da un oggetto User. Questo consente di incapsulare le informazioni sull'utente all'interno di 
    un oggetto CustomUserDetails che può essere utilizzato dal sistema di autenticazione di Spring Security.
L'implementazione specifica dei metodi dell'interfaccia UserDetails all'interno della classe CustomUserDetails 
    non è mostrata nel codice fornito, ma di solito include la restituzione delle informazioni sull'utente 
    come nome utente, password, autorizzazioni e altri dettagli richiesti dal sistema di autenticazione di Spring Security.
I risultati della ricerca forniscono ulteriori informazioni su come creare un oggetto UserDetails personalizzato 
    in Spring Security, inclusi esempi di implementazione e guide dettagliate. */
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
