package com.catalanomanasia.project.controller;

import com.catalanomanasia.project.Utils.RedirectWithMsgUtils;
import com.catalanomanasia.project.model.*;
import com.catalanomanasia.project.service.AppService;
import com.catalanomanasia.project.service.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@SpringBootApplication
public class UserController {

    public static void main(String[] args){
        SpringApplication.run(UserController.class, args);
    }

    public String path;
    public static String apiPath;
    public static String cssPath;
    public static String jsPath;
    public static String assetsPath;
    @Autowired
    private AppService appService;

    // Costruttore di UserController.
    // L'annotazione @Autowired indica che l'oggetto String "path" verrà iniettato tramite l'autowiring.
    // L'annotazione @Value specifica il valore predefinito "http://localhost:9000/" per l'oggetto "path".
    // Quando viene istanziato un oggetto UserController, viene iniettato il valore "http://localhost:9000/" per la variabile "path".
    @Autowired
    UserController(
            @Value("http://localhost:9000/") String path
    ){
        this.path = path;
        cssPath = path + "css/";
        jsPath = path + "js/";
        assetsPath = path + "assets/";
        apiPath = path + "api/";
    }

    // Questo metodo viene utilizzato per inizializzare il modello con gli attributi di percorso ("path"),
    // percorsi per i file CSS ("css"), JS ("js"), risorse (assets) ("assets") e API ("api").
    /*Il model è un componente architetturale del pattern Model-View-Controller (MVC) utilizzato nello sviluppo di sistemi software.
      Il model rappresenta la parte dell'applicazione che gestisce i dati e la logica di business. In particolare,
      il model fornisce i metodi per accedere ai dati utili all'applicazione e si occupa della logica di business.*/
    private void initModel(Model m) {
        m.addAttribute("path", this.path);
        m.addAttribute("css", cssPath);
        m.addAttribute("js", jsPath);
        m.addAttribute("assets", assetsPath);
        m.addAttribute("api", apiPath);
    }

    // Metodo gestore di richieste GET per l'URL "/merchant/new".
    @GetMapping("/merchant/new")
    String newMerchant(Model model) {
        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);

        // Crea un nuovo oggetto 'User' rappresentante un nuovo negoziante (commerciante).
        User newMerchant = new User();

        //NON USO CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //PERCHE' SOLO L'ADMIN PUO' CREARE UN MERCHANT, ED E' SICURO AUTORIZZATO ESSENDO L'ADMIN

        // Ottiene la lista di negozi (store) utilizzando il servizio 'appService'.
        // I negozi verranno visualizzati nella vista per selezionare un negozio associato al nuovo negoziante.
        List<Map> storeList = this.appService.getStores();

        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "merchant-new".
        // - "merchant" con l'oggetto 'newMerchant', rappresentante il nuovo negoziante da creare.
        // - "stores" con la lista dei negozi (storeList) per consentire la selezione di un negozio associato al negoziante.
        model.addAttribute("page", "merchant-new");
        model.addAttribute("merchant", newMerchant);
        model.addAttribute("stores", storeList);

        // Restituisce il nome della vista "merchant/new" (template Thymeleaf) da visualizzare.
        // In questa vista, l'utente potrà inserire i dettagli del nuovo negoziante e selezionare un negozio associato.
        return "merchant/new";
    }


    // Questo metodo gestisce una richiesta GET per l'URL "/customer/new".
    @GetMapping("/customer/new")
    String newCustomer(Model model) {
        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);

        // Crea un nuovo oggetto 'User' rappresentante un nuovo cliente.
        User newCustomer = new User();

        // Ottiene l'oggetto 'CustomUserDetails' dall'oggetto di contesto di sicurezza corrente (SecurityContextHolder).
        // 'CustomUserDetails' è una classe personalizzata che implementa l'interfaccia 'UserDetails' di Spring Security,
        // rappresentante i dettagli dell'utente corrente che sta accedendo all'applicazione.
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Ottiene l'oggetto 'Store' associato all'utente corrente dai dettagli dell'utente.
        Store store = customUserDetails.getStore();

        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "customer-new".
        // - "customer" con l'oggetto 'newCustomer', rappresentante il nuovo cliente da creare.
        // - "storeId" con l'ID dello store associato all'utente corrente.
        // - "storeName" con il nome dello store associato all'utente corrente.
        model.addAttribute("page", "customer-new");
        model.addAttribute("customer", newCustomer);
        model.addAttribute("storeId", store.getId());
        model.addAttribute("storeName", store.getName());

        // Restituisce il nome della vista "customer/new" (template Thymeleaf) da visualizzare.
        return "customer/new";
    }


    // Indica che questo metodo gestisce una richiesta GET per l'URL "/merchant/list".
    @GetMapping("/merchant/list")
    String merchantsList(Model model) {
        // Ottiene una lista di oggetti 'User' rappresentanti i commercianti dal servizio 'appService'.
        List<User> merchantEntityList = this.appService.getMerchants();

        // Crea una nuova lista di mappe (un elenco di oggetti HashMap) per memorizzare le informazioni dei commercianti in un formato più agevole.
        List<Map> merchantList = new ArrayList<>();

        // Cicla attraverso la lista di commercianti ottenuta dal servizio.
        for (User u : merchantEntityList) {
            // Crea una nuova HashMap per memorizzare le informazioni del commerciante attuale.
            HashMap<String, String> userInfo = new HashMap<>();

            // Aggiunge le informazioni del commerciante alla HashMap creata.
            userInfo.put("id", u.getId().toString());
            userInfo.put("firstName", u.getFirstName());
            userInfo.put("lastName", u.getLastName());
            userInfo.put("username", u.getUsername());
            userInfo.put("email", u.getEmail());
            userInfo.put("store", u.getStore().getName());
            userInfo.put("enabled", u.getEnabled() ? "Yes" : "No");

            // Aggiunge la HashMap con le informazioni del commerciante alla lista 'merchantList'.
            merchantList.add(userInfo);
        }

        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model'.
        initModel(model);

        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        model.addAttribute("page", "merchant-list"); // Aggiunge l'attributo "page" con valore "merchant-list".
        model.addAttribute("merchants", merchantList); // Aggiunge l'attributo "merchants" con l'elenco dei commercianti.

        // Restituisce il nome della vista "merchant/list" (template Thymeleaf) da visualizzare.
        return "merchant/list";
    }


    // Questo metodo gestisce una richiesta GET per l'URL "/customer/list".
    @GetMapping("/customer/list")
    String customersList(Model model) {
        // Ottiene gli oggetti CustomUserDetails e Store dell'utente corrente autenticato utilizzando SecurityContextHolder.
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = customUserDetails.getStore();

        // Ottiene la lista di oggetti User rappresentanti i clienti associati allo store tramite il servizio appService.
        List<User> customerEntityList = this.appService.getStoreCustomers(store);

        // Crea una nuova lista di mappe (un elenco di oggetti HashMap) per memorizzare le informazioni dei clienti in un formato più agevole.
        List<Map> customerList = new ArrayList<>();

        // Cicla attraverso la lista di clienti ottenuta dal servizio.
        for (User u : customerEntityList) {
            // Crea una nuova HashMap per memorizzare le informazioni del cliente attuale.
            HashMap<String, String> userInfo = new HashMap<>();

            // Aggiunge le informazioni del cliente alla HashMap creata.
            userInfo.put("id", u.getId().toString());
            userInfo.put("firstName", u.getFirstName());
            userInfo.put("lastName", u.getLastName());
            userInfo.put("username", u.getUsername());
            userInfo.put("email", u.getEmail());
            userInfo.put("enabled", u.getEnabled() ? "Yes" : "No");

            // Aggiunge la HashMap con le informazioni del cliente alla lista 'customerList'.
            customerList.add(userInfo);
        }

        // Ottiene la lista di oggetti CreditCard rappresentanti le carte di credito libere dello store tramite il servizio appService.
        List<CreditCard> freeCreditCardsEntityList = this.appService.getFreeStoreCreditCards(store);

        // Crea una nuova lista di mappe (un elenco di oggetti HashMap) per memorizzare le informazioni delle carte di credito libere in un formato più agevole.
        List<Map> freeCreditCardsList = new ArrayList<>();

        // Cicla attraverso la lista di carte di credito libere ottenuta dal servizio.
        for (CreditCard c : freeCreditCardsEntityList) {
            // Crea una nuova HashMap per memorizzare le informazioni della carta di credito attuale.
            HashMap<String, String> ccInfo = new HashMap<>();
            ccInfo.put("value", c.getId().toString());
            ccInfo.put("label", c.getNumber());

            // Aggiunge la HashMap con le informazioni della carta di credito alla lista 'freeCreditCardsList'.
            freeCreditCardsList.add(ccInfo);
        }

        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model'.
        initModel(model);

        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "customer-list".
        // - "customers" con l'elenco dei clienti rappresentato dalla lista 'customerList'.
        // - "creditCards" con l'elenco delle carte di credito libere rappresentato dalla lista 'freeCreditCardsList'.
        model.addAttribute("page", "customer-list");
        model.addAttribute("customers", customerList);
        model.addAttribute("creditCards", freeCreditCardsList);

        // Restituisce il nome della vista "customer/list" (template Thymeleaf) da visualizzare.
        return "customer/list";
    }


    @PostMapping("/api/merchant/new")
    String createMerchantNew(
            @ModelAttribute User newMerchant,
            HttpSession session
    ){
        // Verifica se esiste già un utente con lo stesso username.
        User existingUser = this.appService.getUserRepository().findByUsername(newMerchant.getUsername());
        if (existingUser != null) {
            // Se esiste già un utente con lo stesso username, reindirizza alla pagina "merchant/new" con un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Username già in uso", "/merchant/new", true);
        }

        // Verifica se esiste già un utente con la stessa email.
        User existingEmailUser = this.appService.getUserRepository().findByEmail(newMerchant.getEmail());
        if (existingEmailUser != null) {
            // Se esiste già un utente con la stessa email, reindirizza alla pagina "merchant/new" con un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Email già in uso", "/merchant/new", true);
        }

        // Crea un oggetto BCryptPasswordEncoder per codificare la password del nuovo negoziante in modo sicuro.
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Recupera il ruolo "ROLE_MERCHANT" dal servizio appService.
        Role merchantRole = this.appService.getRole("ROLE_MERCHANT");

        // Imposta il ruolo del nuovo negoziante come "ROLE_MERCHANT".
        newMerchant.setRole(merchantRole);

        // Imposta il nuovo negoziante come abilitato (enabled = true).
        newMerchant.setEnabled(true);

        // Codifica la password del nuovo negoziante con l'encoder BCrypt e la imposta nell'oggetto newMerchant.
        newMerchant.setPassword(encoder.encode(newMerchant.getPassword()));

        // Salva il nuovo negoziante nel database utilizzando il servizio appService.
        this.appService.save(newMerchant);

        // Reindirizza alla pagina "dashboard" con un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante creato","/dashboard", false);
    }


    @PostMapping("/api/customer/bind-credit-card")
    String bindCreditCard(
            @RequestParam String userId,
            @RequestParam String creditCardId,
            HttpSession session
    ){
        Optional<CreditCard> creditCard = this.appService.getCreditCardRepository().findById(Integer.parseInt(creditCardId));
        Optional <User> customer = this.appService.getUserRepository().findById(Integer.parseInt(userId));

        if(creditCard.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata","/customer/list", true);
        }

        if(customer.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato","/customer/list", true);
        }

        creditCard.get().setOwner(customer.get());
        creditCard.get().setEnabled(1);
        this.appService.save(creditCard.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta assegnata a " + customer.get().getFirstName() + " " + customer.get().getLastName(),"/customer/list", false);
    }

    @PostMapping("/api/customer/new")
    String createCustomerNew(
            @ModelAttribute User newCustomer,
            HttpSession session
    ){
        User existingUser = this.appService.getUserRepository().findByUsername(newCustomer.getUsername());
        if (existingUser != null) {
            return RedirectWithMsgUtils.redirectWithMsg(session, "Username già in uso", "/merchant/new", true);
        }

        User existingEmailUser = this.appService.getUserRepository().findByEmail(newCustomer.getEmail());
        if (existingEmailUser != null) {
            return RedirectWithMsgUtils.redirectWithMsg(session, "Email già in uso", "/merchant/new", true);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Role customerRole = this.appService.getRole("ROLE_CUSTOMER");

        newCustomer.setRole(customerRole);
        newCustomer.setEnabled(true);
        newCustomer.setPassword(encoder.encode(newCustomer.getPassword()));
        this.appService.save(newCustomer);

        Store store = this.getCurrentMerchantStore();

        store.addCustomer(newCustomer);
        this.appService.save(store);

        return RedirectWithMsgUtils.redirectWithMsg(session, "Nuovo cliente creato","/dashboard", false);
    }

    @GetMapping("/api/merchant/delete")
    String deleteMerchant(@RequestParam String id, HttpSession session) {
        // Ottiene l'utente (negoziante) con l'ID specificato tramite il servizio appService.
        Optional<User> user = this.appService.getUserRepository().findById(Integer.parseInt(id));

        // Controlla se l'utente è presente nel database.
        if (user.isEmpty()) {
            // Se l'utente non è stato trovato, reindirizza l'utente alla pagina "/merchant/list" con un messaggio di errore.
            // Il parametro "true" indica che il messaggio è un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato", "/merchant/list", true);
        }

        // Se l'utente è stato trovato, lo elimina dal database tramite il servizio appService.
        this.appService.getUserRepository().delete(user.get());

        // Dopo aver eliminato l'utente, reindirizza l'utente alla pagina "/merchant/list" con un messaggio di successo.
        // Il parametro "false" indica che il messaggio è un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante cancellato", "/merchant/list", false);
    }


    @GetMapping("/api/merchant/disable")
    String disableMerchant(@RequestParam String id, HttpSession session) {
        // Ottiene l'utente (negoziante) con l'ID specificato tramite il servizio appService.
        Optional<User> user = this.appService.getUserRepository().findById(Integer.parseInt(id));

        // Controlla se l'utente è presente nel database.
        if (user.isEmpty()) {
            // Se l'utente non è stato trovato, reindirizza l'utente alla pagina "/merchant/list" con un messaggio di errore.
            // Il parametro "true" indica che il messaggio è un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato", "/merchant/list", true);
        }

        // Imposta il flag 'enabled' dell'utente su 'false' per disabilitare l'account del negoziante.
        user.get().setEnabled(false);

        // Salva l'utente (negoziante) con l'aggiornamento nel database tramite il servizio appService.
        this.appService.save(user.get());

        // Dopo aver disabilitato il negoziante, reindirizza l'utente alla pagina "/merchant/list" con un messaggio di successo.
        // Il parametro "false" indica che il messaggio è un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante disabilitato", "/merchant/list", false);
    }


    @GetMapping("/api/merchant/enable")
    String enableMerchant(@RequestParam String id, HttpSession session) {
        // Ottiene l'utente (negoziante) con l'ID specificato tramite il servizio appService.
        Optional<User> user = this.appService.getUserRepository().findById(Integer.parseInt(id));

        // Controlla se l'utente è presente nel database.
        if (user.isEmpty()) {
            // Se l'utente non è stato trovato, reindirizza l'utente alla pagina "/merchant/list" con un messaggio di errore.
            // Il parametro "true" indica che il messaggio è un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato", "/merchant/list", true);
        }

        // Imposta il flag 'enabled' dell'utente su 'true' per abilitare l'account del negoziante.
        user.get().setEnabled(true);

        // Salva l'utente (negoziante) con l'aggiornamento nel database tramite il servizio appService.
        this.appService.save(user.get());

        // Dopo aver abilitato il negoziante, reindirizza l'utente alla pagina "/merchant/list" con un messaggio di successo.
        // Il parametro "false" indica che il messaggio è un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante abilitato", "/merchant/list", false);
    }


    @GetMapping("/api/customer/delete")
    String customerMerchant(@RequestParam String id, HttpSession session) {
        // Ottiene l'utente (cliente) con l'ID specificato tramite il servizio appService.
        Optional<User> user = this.appService.getUserRepository().findById(Integer.parseInt(id));

        // Controlla se l'utente è presente nel database.
        if (user.isEmpty()) {
            // Se l'utente non è stato trovato, reindirizza l'utente alla pagina "/customer/list" con un messaggio di errore.
            // Il parametro "true" indica che il messaggio è un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non creato", "/customer/list", true);
        }

        // Elimina l'utente (cliente) dal database tramite il servizio appService.
        this.appService.getUserRepository().delete(user.get());

        // Dopo aver eliminato il cliente, reindirizza l'utente alla pagina "/customer/list" con un messaggio di successo.
        // Il parametro "false" indica che il messaggio è un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Cliente eliminato", "/customer/list", false);
    }


    @GetMapping("/api/customer/disable")
    String disableCustomer(@RequestParam String id, HttpSession session, Model model) {
        // Ottiene l'utente (cliente) con l'ID specificato tramite il servizio appService.
        Optional<User> user = this.appService.getUserRepository().findById(Integer.parseInt(id));

        // Controlla se l'utente è presente nel database.
        if (user.isEmpty()) {
            // Se l'utente non è stato trovato, reindirizza l'utente alla pagina "/customer/list" con un messaggio di errore.
            // Il parametro "true" indica che il messaggio è un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato", "/customer/list", true);
        }

        // Imposta il flag 'enabled' dell'utente su 'false' per disabilitare l'account del cliente.
        user.get().setEnabled(false);

        // Salva l'utente (cliente) con l'aggiornamento nel database tramite il servizio appService.
        this.appService.save(user.get());

        // Dopo aver disabilitato il cliente, reindirizza l'utente alla pagina "/customer/list" con un messaggio di successo.
        // Il parametro "false" indica che il messaggio è un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Cliente disabilitato", "/customer/list", false);
    }


    @GetMapping("/api/customer/enable")
    String enableCustomer(@RequestParam String id, HttpSession session) {
        // Ottiene l'utente (cliente) con l'ID specificato tramite il servizio appService.
        Optional<User> user = this.appService.getUserRepository().findById(Integer.parseInt(id));

        // Controlla se l'utente è presente nel database.
        if (user.isEmpty()) {
            // Se l'utente non è stato trovato, reindirizza l'utente alla pagina "/customer/list" con un messaggio di errore.
            // Il parametro "true" indica che il messaggio è un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato!", "/customer/list", true);
        }

        // Imposta il flag 'enabled' dell'utente su 'true' per abilitare l'account del cliente.
        user.get().setEnabled(true);

        // Salva l'utente (cliente) con l'aggiornamento nel database tramite il servizio appService.
        this.appService.save(user.get());

        // Dopo aver abilitato il cliente, reindirizza l'utente alla pagina "/customer/list" con un messaggio di successo.
        // Il parametro "false" indica che il messaggio è un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Cliente abilitato", "/customer/list", false);
    }


    public Store getCurrentMerchantStore() {
        // Ottiene l'oggetto CustomUserDetails dall'oggetto di contesto di sicurezza corrente (SecurityContextHolder).
        // CustomUserDetails è una classe personalizzata che implementa l'interfaccia UserDetails di Spring Security,
        // rappresentante i dettagli dell'utente corrente che sta accedendo all'applicazione.
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Ottiene l'oggetto Store associato all'utente corrente dai dettagli dell'utente.
        // L'oggetto Store rappresenta il negozio (store) al quale il negoziante corrente è associato.
        // Questo metodo è utile poiché consente di ottenere informazioni sul negozio corrente senza dover passare
        // esplicitamente l'oggetto Store come parametro ai vari metodi.
        // L'oggetto Store può contenere informazioni quali ID, nome del negozio, indirizzo, ecc.
        return customUserDetails.getStore();
    }


}
