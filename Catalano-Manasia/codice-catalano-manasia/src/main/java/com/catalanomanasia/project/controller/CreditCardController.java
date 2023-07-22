package com.catalanomanasia.project.controller;

import com.catalanomanasia.project.Utils.RedirectWithMsgUtils;
import com.catalanomanasia.project.model.CreditCard;
import com.catalanomanasia.project.model.Store;
import com.catalanomanasia.project.model.Transaction;
import com.catalanomanasia.project.model.User;
import com.catalanomanasia.project.service.AppService;
import com.catalanomanasia.project.service.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@SpringBootApplication
@RequestMapping("/credit-card")
public class CreditCardController {

    public static void main(String[] args){
        SpringApplication.run(CreditCardController.class, args);
    }

    public String path;
    public static String apiPath;
    public static String cssPath;
    public static String jsPath;
    public static String assetsPath;
    @Autowired
    private AppService appService;

    @Autowired
    CreditCardController(
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

    // Questo metodo gestisce una richiesta GET per l'URL "/list".
// Visualizza la lista di carte di credito associate ai clienti del negozio corrente.
    @GetMapping("/list")
    String creditCardList(Model model) {
        // Ottiene l'utente corrente (negozio) dall'oggetto di autenticazione e recupera il negozio associato.
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = customUserDetails.getStore();

        // Ottiene la lista dei clienti associati al negozio corrente tramite il servizio appService.
        List<User> customerEntityList = this.appService.getStoreCustomers(store);
        List<Map> creditCards = new ArrayList<>();

        // Itera attraverso i clienti e le loro carte di credito per costruire una lista di informazioni sulle carte di credito.
        for (User u : customerEntityList) {
            List<CreditCard> customerCreditCards = this.appService.getCustomerCreditCards(u);
            for (CreditCard c : customerCreditCards) {
                HashMap<String, String> creditCardInfo = new HashMap<>();

                // Popola la mappa con le informazioni della carta di credito (id, numero, saldo, proprietario, email, abilitata).
                creditCardInfo.put("id", c.getId().toString());
                creditCardInfo.put("number", c.getNumber());
                creditCardInfo.put("balance", c.getBalance().toString());
                creditCardInfo.put("owner", c.getOwner().getUsername());
                creditCardInfo.put("email", c.getOwner().getEmail());
                creditCardInfo.put("enabled", c.getEnabled() == 1 ? "Yes" : "No");

                creditCards.add(creditCardInfo);
            }
        }

        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);
        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "credit-card-list".
        // - "creditCards" con la lista delle informazioni delle carte di credito associate ai clienti del negozio.
        model.addAttribute("page", "credit-card-list");
        model.addAttribute("creditCards", creditCards);

        // Restituisce il nome della vista "credit-card/list" (template Thymeleaf) da visualizzare.
        // In questa vista, verrà mostrata la lista delle carte di credito associate ai clienti del negozio.
        return "credit-card/list";
    }

    // Questo metodo gestisce una richiesta GET per l'URL "/block".
// Viene utilizzato per visualizzare il form di blocco di una carta di credito.
    @GetMapping("/block")
    String creditCardBlock(Model model) {
        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);

        // Aggiunge un attributo al modello per renderlo disponibile nella vista (template Thymeleaf).
        // L'attributo aggiunto è:
        // - "page" con valore "credit-card-block".
        model.addAttribute("page", "credit-card-block");

        // Restituisce il nome della vista "credit-card/block" (template Thymeleaf) da visualizzare.
        // In questa vista, l'utente potrà inserire il numero di carta di credito da bloccare.
        return "credit-card/block";
    }

    // Questo metodo gestisce una richiesta GET per l'URL "/new".
// Viene utilizzato per visualizzare il form di creazione di una nuova carta di credito.
    @GetMapping("/new")
    String creditCardNew(Model model) {
        // Ottiene la lista dei negozi (stores) tramite il servizio appService.
        List<Map> stores = this.appService.getStores();

        // Ottiene l'ultima carta di credito registrata tramite il servizio appService.
        CreditCard lastCreditCard = this.appService.getLastCreditCard();
        String numbers = String.format("%04d", lastCreditCard.getId() + 1);
        String newCardNumber = "5000-1234-5678-" + numbers;

        // Crea una nuova istanza di CreditCard e inizializza alcuni campi predefiniti come il numero e lo stato di abilitazione.
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(newCardNumber);
        creditCard.setEnabled(0);

        // Invoca il metodo 'initModel' (non mostrato nel codice fornito) per inizializzare il modello 'model' con attributi comuni.
        initModel(model);
        // Aggiunge alcuni attributi al modello per renderli disponibili nella vista (template Thymeleaf).
        // Gli attributi aggiunti includono:
        // - "page" con valore "credit-card-new".
        // - "creditCard" con l'oggetto 'creditCard', rappresentante la nuova carta di credito da creare.
        // - "stores" con la lista dei negozi (stores) per consentire la selezione di un negozio associato alla carta di credito.
        model.addAttribute("page", "credit-card-new");
        model.addAttribute("creditCard", creditCard);
        model.addAttribute("stores", stores);

        // Restituisce il nome della vista "credit-card/new" (template Thymeleaf) da visualizzare.
        // In questa vista, l'utente potrà inserire i dettagli della nuova carta di credito da creare.
        return "credit-card/new";
    }

    // Questo metodo gestisce una richiesta POST per l'URL "/api/new".
// Viene utilizzato per creare una nuova carta di credito ricevendo i dati dal form di creazione.
    @PostMapping("/api/new")
    String createNewCreditCard(@ModelAttribute CreditCard creditCard, HttpSession session) {
        // Rimuove il carattere "-" dal numero della carta di credito.
        creditCard.setNumber(creditCard.getNumber().replace("-", ""));
        // Imposta lo stato di abilitazione della carta di credito a 1 (abilitata).
        creditCard.setEnabled(1);

        // Salva la nuova carta di credito nel repository tramite il servizio appService.
        this.appService.save(creditCard);

        // Dopo aver creato con successo la carta di credito, reindirizza l'utente alla pagina di dashboard con un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta creata", "/dashboard", false);
    }

    // Questo metodo gestisce una richiesta POST per l'URL "/api/block".
// Viene utilizzato per bloccare o sbloccare una carta di credito ricevendo il numero della carta e il valore di abilitazione dal form di blocco.
    @PostMapping("/api/block")
    String blockCreditCard(@RequestParam String number, @RequestParam String enable, HttpSession session) {
        // Converte il valore di abilitazione (enable) da stringa a intero.
        Integer enableInt = Integer.parseInt(enable);

        // Trova la carta di credito corrispondente al numero fornito tramite il servizio appService.
        CreditCard creditCard = this.appService.getCreditCardRepository().findCreditCardByNumber(number);
        if (creditCard == null) {
            // Se la carta di credito non è stata trovata, reindirizza l'utente alla pagina di blocco con un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata", "/credit-card/block", true);
        }

        if (creditCard.getEnabled() == enableInt) {
            // Se lo stato di abilitazione della carta corrisponde al valore di abilitazione fornito, reindirizza l'utente alla pagina di blocco con un messaggio informativo.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta già in questo stato", "/credit-card/block", true);
        }

        // Imposta il nuovo stato di abilitazione della carta di credito e salva le modifiche nel repository tramite il servizio appService.
        creditCard.setEnabled(enableInt);
        this.appService.save(creditCard);

        // Dopo aver bloccato o sbloccato con successo la carta di credito, reindirizza l'utente alla pagina di dashboard con un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta bloccata", "/dashboard", false);
    }

    // Questo metodo gestisce una richiesta POST per l'URL "/api/recharge-card".
// Viene utilizzato per ricaricare una carta di credito ricevendo l'ID della carta e l'importo di ricarica dal form di ricarica.
    @PostMapping("/api/recharge-card")
    String rechargeCreditCard(@RequestParam String rechargeCreditCardId, @RequestParam String rechargeAmount, HttpSession session) {
        // Trova la carta di credito corrispondente all'ID fornito tramite il servizio appService.
        Optional<CreditCard> creditCard = this.appService.getCreditCardRepository().findById(Integer.parseInt(rechargeCreditCardId));

        if (creditCard.isEmpty()) {
            // Se la carta di credito non è stata trovata, reindirizza l'utente alla pagina della lista delle carte di credito con un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata", "/credit-card/list", true);
        }

        // Incrementa il saldo della carta di credito con l'importo di ricarica e salva le modifiche nel repository tramite il servizio appService.
        creditCard.get().setBalance(creditCard.get().getBalance() + Integer.parseInt(rechargeAmount));
        this.appService.save(creditCard.get());

        // Crea una nuova transazione per la ricarica e salva la transazione nel repository tramite il servizio appService.
        Transaction t = new Transaction();
        t.setCreditCard(creditCard.get());
        t.setType("RECHARGE");
        t.setTime(new Date());
        t.setAmount(Integer.parseInt(rechargeAmount));
        this.appService.save(t);

        // Dopo aver ricaricato con successo la carta di credito, reindirizza l'utente alla pagina della lista delle carte di credito con un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta ricaricata", "/credit-card/list", false);
    }

    // Questo metodo gestisce una richiesta POST per l'URL "/api/charge-card".
// Viene utilizzato per effettuare un pagamento con una carta di credito ricevendo l'ID della carta e l'importo del pagamento dal form di pagamento.
    @PostMapping("/api/charge-card")
    String chargeCreditCard(@RequestParam String purchaseCreditCardId, @RequestParam String purchaseAmount, HttpSession session) {
        // Trova la carta di credito corrispondente all'ID fornito tramite il servizio appService.
        Optional<CreditCard> creditCard = this.appService.getCreditCardRepository().findById(Integer.parseInt(purchaseCreditCardId));

        if (creditCard.isEmpty()) {
            // Se la carta di credito non è stata trovata, reindirizza l'utente alla pagina della lista delle carte di credito con un messaggio di errore.
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata", "/credit-card/list", true);
        }

        // Decrementa il saldo della carta di credito con l'importo del pagamento e salva le modifiche nel repository tramite il servizio appService.
        creditCard.get().setBalance(creditCard.get().getBalance() - Integer.parseInt(purchaseAmount));
        this.appService.save(creditCard.get());

        // Crea una nuova transazione per il pagamento e salva la transazione nel repository tramite il servizio appService.
        Transaction t = new Transaction();
        t.setCreditCard(creditCard.get());
        t.setType("PURCHASE");
        t.setTime(new Date());
        t.setAmount(Integer.parseInt(purchaseAmount));
        this.appService.save(t);

        // Dopo aver effettuato con successo il pagamento con la carta di credito, reindirizza l'utente alla pagina della lista delle carte di credito con un messaggio di successo.
        return RedirectWithMsgUtils.redirectWithMsg(session, "Pagamento effettuato", "/credit-card/list", false);
    }


}
