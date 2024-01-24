package com.example.NodeVM.controller;

import com.example.NodeVM.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/validation")
public class ValidatorController {
    private ValidatorService validatorService;

    @Autowired
    public ValidatorController(ValidatorService validatorService){

        this.validatorService=validatorService;
    }

    @GetMapping("/isAdmin/{username}/{token}")
    public boolean isAdmin(@PathVariable String username, @PathVariable String token){
        return validatorService.isAdmin(username, token);
    }

    @GetMapping("/isValidUser/{username}/{token}")
    public boolean isValidUser(@PathVariable String username, @PathVariable String token){
        return validatorService.isValidUser(username, token);
    }

}
