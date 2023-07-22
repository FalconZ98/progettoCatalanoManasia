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

    @GetMapping("/list")
    String creditCardList(Model model){
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = customUserDetails.getStore();

        List<User> customerEntityList = this.appService.getStoreCustomers(store);
        List<Map> creditCards = new ArrayList<>();

        for(User u : customerEntityList){
            List<CreditCard> customerCreditCards = this.appService.getCustomerCreditCards(u);
            for(CreditCard c : customerCreditCards){
                HashMap<String, String> creditCardInfo = new HashMap<>();

                creditCardInfo.put("id", c.getId().toString());
                creditCardInfo.put("number", c.getNumber());
                creditCardInfo.put("balance", c.getBalance().toString());
                creditCardInfo.put("owner", c.getOwner().getUsername());
                creditCardInfo.put("email", c.getOwner().getEmail());
                creditCardInfo.put("enabled", c.getEnabled() == 1 ? "Yes" : "No");

                creditCards.add(creditCardInfo);
            }
        }

        initModel(model);
        model.addAttribute("page", "credit-card-list");
        model.addAttribute("creditCards", creditCards);

        return "credit-card/list";
    }

    @GetMapping("/block")
    String creditCardBlock(Model model){
        initModel(model);
        model.addAttribute("page", "credit-card-block");
        return "credit-card/block";
    }

    @GetMapping("/new")
    String creditCardNew(Model model){
        List<Map> stores = this.appService.getStores();

        CreditCard lastCreditCard = this.appService.getLastCreditCard();
        String numbers = String.format("%04d", lastCreditCard.getId() + 1);
        String newCardNumber = "5000-1234-5678-" + numbers;

        CreditCard creditCard = new CreditCard();

        creditCard.setNumber(newCardNumber);
        creditCard.setEnabled(0);

        initModel(model);
        model.addAttribute("page", "credit-card-new");
        model.addAttribute("creditCard", creditCard);
        model.addAttribute("stores", stores);
        return "credit-card/new";
    }

    @PostMapping("/api/new")
    String createNewCreditCard(@ModelAttribute CreditCard creditCard, HttpSession session){
        creditCard.setNumber(creditCard.getNumber().replace("-",""));
        creditCard.setEnabled(1);
        this.appService.save(creditCard);
        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta creata","/dashboard", false);
    }

    @PostMapping("/api/block")
    String blockCreditCard(@RequestParam String number, @RequestParam String enable, HttpSession session){
        Integer enableInt = Integer.parseInt(enable);
        CreditCard creditCard = this.appService.getCreditCardRepository().findCreditCardByNumber(number);
        if(creditCard == null){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata","/credit-card/block", true);
        }
        if(creditCard.getEnabled() == enableInt){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta già in questo stato","/credit-card/block", true);
        }
        creditCard.setEnabled(enableInt);
        this.appService.save(creditCard);
        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta bloccata","/dashboard", false);
    }

    @PostMapping("/api/recharge-card")
    String rechargeCreditCard(
            @RequestParam String rechargeCreditCardId,
            @RequestParam String rechargeAmount,
            HttpSession session
    ){
        Optional<CreditCard> creditCard = this.appService.getCreditCardRepository().findById(Integer.parseInt(rechargeCreditCardId));

        if(creditCard.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata","/credit-card/list", true);
        }

        creditCard.get().setBalance(creditCard.get().getBalance() + Integer.parseInt(rechargeAmount));
        this.appService.save(creditCard.get());

        Transaction t = new Transaction();
        t.setCreditCard(creditCard.get());
        t.setType("RECHARGE");
        t.setTime(new Date());
        t.setAmount(Integer.parseInt(rechargeAmount));
        this.appService.save(t);

        return RedirectWithMsgUtils.redirectWithMsg(session, "Carta ricaricata","/credit-card/list", false);
    }

    @PostMapping("/api/charge-card")
    String chargeCreditCard(
            @RequestParam String purchaseCreditCardId,
            @RequestParam String purchaseAmount,
            HttpSession session
    ){
        Optional <CreditCard> creditCard = this.appService.getCreditCardRepository().findById(Integer.parseInt(purchaseCreditCardId));

        if(creditCard.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Carta non trovata","/credit-card/list", true);
        }

        creditCard.get().setBalance(creditCard.get().getBalance() - Integer.parseInt(purchaseAmount));
        this.appService.save(creditCard.get());

        Transaction t = new Transaction();
        t.setCreditCard(creditCard.get());
        t.setType("PURCHASE");
        t.setTime(new Date());
        t.setAmount(Integer.parseInt(purchaseAmount));
        this.appService.save(t);

        return RedirectWithMsgUtils.redirectWithMsg(session, "Pagamento effettuato","/credit-card/list", false);
    }

}
