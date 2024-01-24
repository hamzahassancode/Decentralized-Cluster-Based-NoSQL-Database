package com.example.Bootstrap.controller;

import com.example.Bootstrap.service.BootstrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/bootstrap")
public class BootstrapController {
    private BootstrapService bootstrapService;

    @Autowired
    private BootstrapController(BootstrapService bootstrapService){
        this.bootstrapService=bootstrapService;
    }

    @GetMapping("/registerNewUser/{username}")
    public String registerNewUser(@PathVariable String username){
    return bootstrapService.registerNewUser(username);
    }

    @GetMapping("getAllUsers")
    public String getAllUsers() {
        return bootstrapService.getAllUsers();
    }

    @GetMapping("removeUser/{token}")
    public void removeUser(@PathVariable("token") String token) {

        bootstrapService.removeUser(token);
    }




}
