package com.catalanomanasia.project.controller;

import com.catalanomanasia.project.model.CreditCard;
import com.catalanomanasia.project.model.Store;
import com.catalanomanasia.project.model.Transaction;
import com.catalanomanasia.project.model.User;
import com.catalanomanasia.project.service.AppService;
import com.catalanomanasia.project.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Controller
public class FrontEndController {
    public static void main(String[] args){
        SpringApplication.run(FrontEndController.class, args);
    }

    public String path;
    public static String apiPath;
    public static String cssPath;
    public static String jsPath;
    public static String assetsPath;
    @Autowired
    private AppService appService;

    @Autowired
    FrontEndController(
            @Value("${spring.base}") String path
    ){
        this.path = path;
        cssPath = path + "css/";
        jsPath = path + "js/";
        assetsPath = path + "assets/";
        apiPath = path + "api/";
    }

    @PostMapping("/balance")
    String getCardBalance(@RequestParam String number, Model model){
        CreditCard cc = this.appService.getCreditCardByNumber(number);
        if(cc == null){
            return "redirect:/?err";
        }
        model.addAttribute("balance", cc.getBalance());
        return index(model);
    }
    private void initModel(Model m){
        m.addAttribute("path", this.path);
        m.addAttribute("css", cssPath);
        m.addAttribute("js", jsPath);
        m.addAttribute("assets", assetsPath);
        m.addAttribute("api", apiPath);
    }
    @GetMapping("/")
    String index(Model model){
        initModel(model);
        return "index";
    }

    @GetMapping("/login")
    String login(Model model){
        initModel(model);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null  && !(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS")))){
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/logout")
    String logout(){
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    String dashboard(Model model){
        initModel(model);
        model.addAttribute("page", "dashboard");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null  && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            List<Transaction> transactionEntityList = this.appService.getAllTransactions();
            List<Map> transactionList = new ArrayList<>();
            for(Transaction t : transactionEntityList){
                HashMap<String, String> transactionInfo = new HashMap<>();


                transactionInfo.put("id", t.getId().toString());
                transactionInfo.put("type", t.getType());
                transactionInfo.put("customer", t.getCreditCard().getOwner().getUsername());
                transactionInfo.put("store", t.getCreditCard().getStore().getName());
                transactionInfo.put("time", t.getTime().toString());
                transactionInfo.put("amount", t.getAmount().toString());

                transactionList.add(transactionInfo);
            }

            model.addAttribute("transactions", transactionList);
        }
        else if(auth != null  && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MERCHANT"))){
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Store store = customUserDetails.getStore();
            List<Transaction> transactionEntityList = this.appService.getStoreTransactions(store);

            List<Map> transactionList = new ArrayList<>();
            for(Transaction t : transactionEntityList){
                HashMap<String, String> transactionInfo = new HashMap<>();

                transactionInfo.put("id", t.getId().toString());
                transactionInfo.put("type", t.getType());
                transactionInfo.put("customer", t.getCreditCard().getOwner().getEmail());
                transactionInfo.put("time", t.getTime().toString());
                transactionInfo.put("amount", t.getAmount().toString());

                transactionList.add(transactionInfo);
            }

            model.addAttribute("transactions", transactionList);
        }
        else if(auth != null  && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))){
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<Transaction> transactionEntityList = this.appService.getUserTransactions(customUserDetails.getUser());

            List<Map> transactionList = new ArrayList<>();
            for(Transaction t : transactionEntityList){
                HashMap<String, String> transactionInfo = new HashMap<>();

                transactionInfo.put("id", t.getId().toString());
                transactionInfo.put("type", t.getType());
                transactionInfo.put("store", t.getCreditCard().getStore().getName());
                transactionInfo.put("time", t.getTime().toString());
                transactionInfo.put("amount", t.getAmount().toString());

                transactionList.add(transactionInfo);
            }

            model.addAttribute("transactions", transactionList);
        }


        return "dashboard";
    }


}
