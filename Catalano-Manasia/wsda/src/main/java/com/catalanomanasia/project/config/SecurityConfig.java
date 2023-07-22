package com.catalanomanasia.project.config;

import com.catalanomanasia.project.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    DataSource dataSource;

    @Bean UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/",
                                "/css/**",
                                "/assets/**",
                                "/js/**",
                                "/favicon.ico",
                                "/balance**",
                                "/testpdf"
                                ).permitAll()
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/get_card_balance").permitAll()
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/credit-card/new",
                                "/api/credit-card/new",
                                "/merchant/new",
                                "/api/merchant/new",
                                "/api/merchant/disable**",
                                "/api/merchant/enable**",
                                "/api/merchant/delete**",
                                "/store/new",
                                "/api/store/new",
                                "/store/delete",
                                "/api/store/delete"
                        ).hasAnyAuthority("ROLE_ADMIN")
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/customer/new",
                                "/customer/list",
                                "/api/customer/new",
                                "/api/customer/bind-credit-card",
                                "/api/customer/disable**",
                                "/api/customer/enable**",
                                "/api/customer/delete**",
                                "/api/recharge-card**",
                                "/api/charge-card**"
                        ).hasAnyAuthority("ROLE_MERCHANT")
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/dashboard").hasAnyAuthority("ROLE_ADMIN","ROLE_MERCHANT","ROLE_CUSTOMER")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler(){
        return new CustomSuccessHandler();
    }
}
