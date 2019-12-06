package com.ljtao.sbflexiblea.web.controller;


import com.ljtao.sbflexiblea.domian.master.User;
import com.ljtao.sbflexiblea.service.master.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {
    @Autowired
    private UserService userServer;
    @RequestMapping("/fun1")
    public String fun1(){
        return "success!";
    }
    @RequestMapping("/fun2")
    public User fun2(){
        return userServer.findByName("1");
    }
}
