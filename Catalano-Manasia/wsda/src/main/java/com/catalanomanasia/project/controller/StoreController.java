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

    @GetMapping("/new")
    String newStore(Model model){
        initModel(model);
        Store store =  new Store();

        model.addAttribute("page", "store-new");
        model.addAttribute("store", store);
        return "store/new";
    }

    @PostMapping("/api/new")
    String newStore(@ModelAttribute Store store, HttpSession session){
        Optional<Store> existingStore = this.appService.getStoreRepository().findByName(store.getName());

        if(existingStore.isPresent()){
            return RedirectWithMsgUtils.redirectWithMsg(session, "Store with this name already exists","/store/new", true);
        }

        this.appService.save(store);
        return RedirectWithMsgUtils.redirectWithMsg(session, "Store created","/dashboard", false);
    }

    @GetMapping("/delete")
    String deleteStore(Model model){
        initModel(model);

        List<Map> storesList = this.appService.getStores();

        model.addAttribute("page", "store-delete");
        model.addAttribute("stores", storesList);
        return "store/delete";
    }

    @PostMapping("/api/delete")
    String deleteStore(@RequestParam String id, HttpSession session){
        Integer intId = Integer.parseInt(id);
        Optional<Store> store = this.appService.getStoreRepository().findById(intId);

        if(store.isPresent()){
            this.appService.getStoreRepository().delete(store.get());
            return RedirectWithMsgUtils.redirectWithMsg(session, "Store delete","/dashboard", false);
        }

        return RedirectWithMsgUtils.redirectWithMsg(session, "Store not found","/dashboard", true);
    }


}
