package com.catalanomanasia.project.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    // Utilizziamo un logger per registrare gli eventi di questa classe
    protected Log logger = LogFactory.getLog(this.getClass());

    // Definiamo la strategia di reindirizzamento predefinita per gestire i reindirizzamenti
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // Questo metodo viene chiamato quando l'autenticazione ha avuto successo
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        // Chiamiamo il metodo 'handle' per gestire l'autenticazione con successo
        handle(request, response, authentication);

        // Chiamiamo il metodo 'clearAuthenticationAttributes' per rimuovere eventuali attributi di autenticazione residui
        // dalla sessione
        clearAuthenticationAttributes(request);
    }

    // Questo metodo gestisce il reindirizzamento dopo un'effettuata autenticazione con successo
    protected void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // Determiniamo l'URL target in base al ruolo dell'utente autenticato
        String targetUrl = determineTargetUrl(authentication);

        // Se la risposta HTTP è già stata commessa, non possiamo effettuare il reindirizzamento
        if (response.isCommitted()) {
            logger.debug(
                    "Response has already been committed. Unable to redirect to "
                            + targetUrl);
            return;
        }

        // Effettuiamo il reindirizzamento alla pagina target
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    // Questo metodo determina l'URL target in base al ruolo dell'utente autenticato
    // Questo metodo prende in input un oggetto di tipo Authentication, che rappresenta l'utente autenticato
    protected String determineTargetUrl(final Authentication authentication) {

        // Creiamo una mappa che associa i ruoli (chiavi) con gli URL di destinazione (valori)
        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_CUSTOMER", "/dashboard");
        roleTargetUrlMap.put("ROLE_MERCHANT", "/dashboard");
        roleTargetUrlMap.put("ROLE_ADMIN", "/dashboard");

        // Otteniamo la lista delle autorizzazioni (ruoli) dell'utente autenticato
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Iteriamo attraverso le autorizzazioni dell'utente per determinare l'URL di destinazione in base al ruolo
        for (final GrantedAuthority grantedAuthority : authorities) {
            // Otteniamo il nome dell'autorizzazione corrente (ruolo)
            String authorityName = grantedAuthority.getAuthority();

            // Verifichiamo se la mappa contiene l'autorizzazione corrente (ruolo)
            if (roleTargetUrlMap.containsKey(authorityName)) {
                // Se troviamo una corrispondenza tra il ruolo e l'URL di destinazione, restituiamo quest'ultimo
                return roleTargetUrlMap.get(authorityName);
            }
        }

        // Se il ruolo dell'utente non corrisponde a nessun URL mappato, lanciamo un'eccezione
        throw new IllegalStateException();
    }


    // Questo metodo rimuove gli attributi di autenticazione dalla sessione
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        // Otteniamo la sessione corrente. Se non esiste, non dobbiamo fare nulla
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        // Rimuoviamo eventuali attributi relativi all'errore di autenticazione dalla sessione
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}

