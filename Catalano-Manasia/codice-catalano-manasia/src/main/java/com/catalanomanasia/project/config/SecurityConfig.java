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

    /*DataSource è un'interfaccia di Spring Framework che rappresenta la connessione a un database.
    L'annotazione @Autowired indica a Spring di cercare un'istanza di DataSource nel contesto dell'applicazione e
    di iniettarla automaticamente nella variabile dataSource.*/
    @Autowired
    DataSource dataSource;

    // Definisce un servizio personalizzato per recuperare i dettagli dell'utente durante l'autenticazione.
    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    // Definisce l'encoder di password BCrypt da utilizzare per crittografare le password degli utenti.
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Definisce un provider di autenticazione basato su DAO, che utilizza il servizio CustomUserDetailsService e
    // BCryptPasswordEncoder.
    // Questa annotazione indica che il seguente metodo definisce un bean che sarà gestito dal container Spring.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Creazione di un nuovo oggetto DaoAuthenticationProvider.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Impostazione del servizio che gestisce i dettagli dell'utente.
        // È fondamentale fornire un'implementazione personalizzata di UserDetailsService
        // che carica i dati dell'utente dal database o da una fonte dati esterna.
        authProvider.setUserDetailsService(userDetailsService());

        // Impostazione dell'encoder per le password degli utenti.
        // L'encoder viene utilizzato per crittografare le password durante la registrazione
        // e per confrontare le password immesse dall'utente durante l'autenticazione.
        authProvider.setPasswordEncoder(passwordEncoder());

        // Restituzione del DaoAuthenticationProvider configurato.
        return authProvider;
    }


    // Definisce l'AuthenticationManager utilizzando l'oggetto AuthenticationConfiguration.
    // Questo bean viene utilizzato per l'autenticazione personalizzata nelle altre parti dell'applicazione.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Definisce una catena di filtri di sicurezza basata sulle regole specificate.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                // Definisce le regole di autorizzazione per determinati URL o percorsi della nostra applicazione.
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
                // Configura il form di login personalizzato e la pagina di login.
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler()) // Imposta l'handler per il successo dell'autenticazione.
                        .permitAll()
                )
                // Configura il logout dalla sessione.
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build(); // Restituisce la catena di filtri di sicurezza configurata.
    }

    // Definisce un gestore per il successo dell'autenticazione.
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler(){
        return new CustomSuccessHandler();
    }
}
