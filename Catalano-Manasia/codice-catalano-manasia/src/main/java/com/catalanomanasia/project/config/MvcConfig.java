package com.catalanomanasia.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration //L'annotazione @Configuration indica che questa classe è una classe di configurazione di Spring.
public class MvcConfig { //La classe MvcConfig è utilizzata per configurare le viste all'interno dell'applicazione web.

    // Il metodo addViewControllers() viene utilizzato per aggiungere le viste al registro delle viste (ViewControllerRegistry).
    public void addViewControllers(ViewControllerRegistry registry){
        //registry.addViewController("/").setViewName("index") mappa l'URL "/" alla vista "index".
        // Questo significa che quando l'utente accede all'URL "/", verrà visualizzata la vista "index".
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/dashboard").setViewName("dashboard");
        registry.addViewController("/credit-card/list").setViewName("/credit-card/list");
    }
}
