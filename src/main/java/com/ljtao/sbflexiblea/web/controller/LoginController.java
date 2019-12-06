package com.ljtao.sbflexiblea.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
    @RequestMapping("/login.page")
    public String loginPage(){

        return "login";
    }
    @RequestMapping("/index")
    public String indexPage(){
        return "index";
    }
}
