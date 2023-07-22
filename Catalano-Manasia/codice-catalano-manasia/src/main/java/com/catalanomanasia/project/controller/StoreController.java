package com.catalanomanasia.project.controller;

import com.catalanomanasia.project.model.Store;
import com.catalanomanasia.project.service.AppService;
import com.catalanomanasia.project.Utils.RedirectWithMsgUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@SpringBootApplication
@RequestMapping("/store")
public class StoreController {
    public static void main(String[] args){
        SpringApplication.run(StoreController.class, args);
    }

    public String path;
    public static String apiPath;
    public static String cssPath;
    public static String jsPath;
    public static String assetsPath;
    @Autowired
    private AppService appService;

    @Autowired
    StoreController(
            @Value("http://localhost:9000/") String path
    ){
        this.path = path;
        cssPath = path + "css/";
        jsPath = path + "js/";
        assetsPath = path + "assets/";
        apiPath = path + "api/";
    }

    private void initModel(Model m){
        m.addAttribute("path", this.path);
        m.addAttribute("css", cssPath);
        m.addAttribute("js", jsPath);
        m.addAttribute("assets", assetsPath);
        m.addAttribute("api", apiPath);
    }

    // Questo metodo gestisce una richiesta GET per l'URL "/new".
// Viene utilizzato per visualizzare il form di creazione di un nuovo negozio (store).
    @GetMapping("/new")
    String newStore(Model model) {
        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);

        // Crea un nuovo oggetto 'Store' rappresentante un nuovo negozio.
        Store store = new Store();

        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "store-new".
        // - "store" con l'oggetto 'store', rappresentante il nuovo negozio da creare.
        model.addAttribute("page", "store-new");
        model.addAttribute("store", store);

        // Restituisce il nome della vista "store/new" (template Thymeleaf) da visualizzare.
        // In questa vista, l'utente potrà inserire i dettagli del nuovo negozio da creare.
        return "store/new";
    }

    // Questo metodo gestisce una richiesta POST per l'URL "/api/new".
// Viene utilizzato per creare un nuovo negozio (store) ricevendo i dati dal form di creazione.
    @PostMapping("/api/new")
    String newStore(@ModelAttribute Store store, HttpSession session) {
        // Controlla se esiste già un negozio con lo stesso nome nel repository.
        Optional<Store> existingStore = this.appService.getStoreRepository().findByName(store.getName());

        // Se esiste già un negozio con lo stesso nome, mostra un messaggio di errore.
        if (existingStore.isPresent()) {
            return RedirectWithMsgUtils.redirectWithMsg(session, "Store with this name already exists", "/store/new", true);
        }

        // Salva il nuovo negozio nel repository tramite il servizio appService.
        this.appService.save(store);

        // Dopo aver creato con successo il negozio, reindirizza l'utente alla pagina di dashboard con un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Store created", "/dashboard", false);
    }

    // Questo metodo gestisce una richiesta GET per l'URL "/delete".
// Viene utilizzato per visualizzare il form di eliminazione di un negozio (store).
    @GetMapping("/delete")
    String deleteStore(Model model) {
        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);

        // Ottiene la lista di negozi (stores) tramite il servizio appService.
        List<Map> storesList = this.appService.getStores();

        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "store-delete".
        // - "stores" con la lista dei negozi (storesList) per consentire la selezione di un negozio da eliminare.
        model.addAttribute("page", "store-delete");
        model.addAttribute("stores", storesList);

        // Restituisce il nome della vista "store/delete" (template Thymeleaf) da visualizzare.
        // In questa vista, l'utente potrà selezionare un negozio da eliminare.
        return "store/delete";
    }

    // Questo metodo gestisce una richiesta POST per l'URL "/api/delete".
// Viene utilizzato per eliminare un negozio (store) ricevendo l'ID del negozio da eliminare.
    @PostMapping("/api/delete")
    String deleteStore(@RequestParam String id, HttpSession session) {
        // Converte l'ID (in formato stringa) del negozio da eliminare in un numero intero.
        Integer intId = Integer.parseInt(id);

        // Cerca il negozio nel repository tramite il suo ID.
        Optional<Store> store = this.appService.getStoreRepository().findById(intId);

        // Se il negozio è presente, procede con l'eliminazione.
        if (store.isPresent()) {
            this.appService.getStoreRepository().delete(store.get());

            // Dopo aver eliminato con successo il negozio, reindirizza l'utente alla pagina di dashboard con un messaggio di successo.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Store deleted", "/dashboard", false);
        }

        // Se il negozio non è stato trovato, reindirizza l'utente alla pagina di dashboard con un messaggio di errore.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Store not found", "/dashboard", true);
    }



}
