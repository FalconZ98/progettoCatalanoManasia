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

    private void initModel(Model m){
        m.addAttribute("path", this.path);
        m.addAttribute("css", cssPath);
        m.addAttribute("js", jsPath);
        m.addAttribute("assets", assetsPath);
        m.addAttribute("api", apiPath);
    }

    @GetMapping("/merchant/new")
    String newMerchant(Model model){
        initModel(model);
        User newMerchant =  new User();

        List<Map> storeList = this.appService.getStores();
        model.addAttribute("page", "merchant-new");
        model.addAttribute("merchant", newMerchant);
        model.addAttribute("stores",storeList);
        return "merchant/new";
    }

    @GetMapping("/customer/new")
    String newCustomer(Model model){
        initModel(model);
        User newCustomer =  new User();

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = customUserDetails.getStore();

        model.addAttribute("page", "customer-new");
        model.addAttribute("customer", newCustomer);
        model.addAttribute("storeId", store.getId());
        model.addAttribute("storeName", store.getName());
        return "customer/new";
    }

    @GetMapping("/merchant/list")
    String merchantsList(Model model){
        List<User> merchantEntityList = this.appService.getMerchants();
        List<Map> merchantList = new ArrayList<>();

        for(User u : merchantEntityList){
            HashMap<String, String> userInfo = new HashMap<>();

            userInfo.put("id", u.getId().toString());
            userInfo.put("firstName", u.getFirstName());
            userInfo.put("lastName", u.getLastName());
            userInfo.put("username", u.getUsername());
            userInfo.put("email", u.getEmail());
            userInfo.put("store", u.getStore().getName());
            userInfo.put("enabled", u.getEnabled() ? "Yes" : "No");

            merchantList.add(userInfo);
        }

        initModel(model);
        model.addAttribute("page", "merchant-list");
        model.addAttribute("merchants", merchantList);
        return "merchant/list";
    }

    @GetMapping("/customer/list")
    String customersList(Model model){
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = customUserDetails.getStore();

        List<User> customerEntityList = this.appService.getStoreCustomers(store);
        List<Map> customerList = new ArrayList<>();

        for(User u : customerEntityList){
            HashMap<String, String> userInfo = new HashMap<>();

            userInfo.put("id", u.getId().toString());
            userInfo.put("firstName", u.getFirstName());
            userInfo.put("lastName", u.getLastName());
            userInfo.put("username", u.getUsername());
            userInfo.put("email", u.getEmail());
            userInfo.put("enabled", u.getEnabled() ? "Yes" : "No");

            customerList.add(userInfo);
        }

        List<CreditCard> freeCreditCardsEntityList = this.appService.getFreeStoreCreditCards(store);

        List<Map> freeCreditCardsList = new ArrayList<>();

        for(CreditCard c : freeCreditCardsEntityList){
            HashMap<String, String> ccInfo = new HashMap<>();
            ccInfo.put("value", c.getId().toString());
            ccInfo.put("label", c.getNumber());

            freeCreditCardsList.add(ccInfo);
        }

        initModel(model);
        model.addAttribute("page", "customer-list");
        model.addAttribute("customers", customerList);
        model.addAttribute("creditCards", freeCreditCardsList);
        return "customer/list";
    }

    @PostMapping("/api/merchant/new")
    String createMerchantNew(
            @ModelAttribute User newMerchant,
            HttpSession session
    ){
        User existingUser = this.appService.getUserRepository().findByUsername(newMerchant.getUsername());
        if (existingUser != null) {
            return RedirectWithMsgUtils.redirectWithMsg(session, "Username già in uso", "/merchant/new", true);
        }

        User existingEmailUser = this.appService.getUserRepository().findByEmail(newMerchant.getEmail());
        if (existingEmailUser != null) {
            return RedirectWithMsgUtils.redirectWithMsg(session, "Email già in uso", "/merchant/new", true);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Role merchantRole = this.appService.getRole("ROLE_MERCHANT");

        newMerchant.setRole(merchantRole);
        newMerchant.setEnabled(true);
        newMerchant.setPassword(encoder.encode(newMerchant.getPassword()));
        this.appService.save(newMerchant);

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
    String deleteMerchant(@RequestParam String id, HttpSession session){
        Optional<User> user = this.appService.getUserRepository().findById((Integer.parseInt(id)));

        if(user.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato","/merchant/list", true);
        }
        this.appService.getUserRepository().delete(user.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante cancellato","/merchant/list", false);
    }

    @GetMapping("/api/merchant/disable")
    String disableMerchant(@RequestParam String id, HttpSession session){
        Optional<User> user = this.appService.getUserRepository().findById((Integer.parseInt(id)));

        if(user.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato","/merchant/list", true);
        }

        user.get().setEnabled(false);
        this.appService.save(user.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante disabilitato","/merchant/list", false);
    }

    @GetMapping("/api/merchant/enable")
    String enableMerchant(@RequestParam String id, HttpSession session){
        Optional<User> user = this.appService.getUserRepository().findById((Integer.parseInt(id)));

        if(user.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato","/merchant/list", true);
        }

        user.get().setEnabled(true);
        this.appService.save(user.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Negoziante abilitato","/merchant/list", false);
    }

    @GetMapping("/api/customer/delete")
    String customerMerchant(@RequestParam String id, HttpSession session){
        Optional<User> user = this.appService.getUserRepository().findById((Integer.parseInt(id)));

        if(user.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non creato","/customer/list", true);
        }

        this.appService.getUserRepository().delete(user.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Cliente eliminato","/customer/list", false);
    }

    @GetMapping("/api/customer/disable")
    String disableCustomer(@RequestParam String id, HttpSession session, Model model){
        Optional<User> user = this.appService.getUserRepository().findById((Integer.parseInt(id)));

        if(user.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato","/customer/list", true);
        }

        user.get().setEnabled(false);
        this.appService.save(user.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Cliente disabilitato","/customer/list", false);
    }

    @GetMapping("/api/customer/enable")
    String enableCustomer(@RequestParam String id, HttpSession session){
        Optional<User> user = this.appService.getUserRepository().findById((Integer.parseInt(id)));

        if(user.isEmpty()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Utente non trovato!","/customer/list", true);
        }
        user.get().setEnabled(true);
        this.appService.save(user.get());

        return RedirectWithMsgUtils.redirectWithMsg(session, "Cliente abilitato","/customer/list", false);
    }

    public Store getCurrentMerchantStore(){
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUserDetails.getStore();
    }

}
