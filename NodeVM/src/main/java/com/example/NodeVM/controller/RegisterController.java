package com.example.NodeVM.controller;

import com.example.NodeVM.model.Response;
import com.example.NodeVM.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/register")
public class RegisterController {
    private RegisterService registerService;
    @Autowired
    public RegisterController(RegisterService registerService){
        this.registerService=registerService;
    }

/**
    communication with bootstrap
 **/
    @GetMapping("/addNewUserToNode/{username}/{token}")
    public Response addNewUserToNode(@PathVariable(value = "username") String username,
                               @PathVariable(value = "token") String token,
                               @RequestHeader(value = "username")String adminUsername,
                               @RequestHeader(value = "token")String adminToken) throws IOException {

        return registerService.addNewUserToNode(username,token,adminUsername,adminToken);
    }

    @DeleteMapping("/removeUserFromNode/{username}/{token}")
    public Response removeUserFromNode(
            @PathVariable("username") String username,
            @PathVariable("token") String token,
            @RequestHeader(value = "username") String adminUsername,
            @RequestHeader(value = "token") String adminToken) throws IOException {

        return registerService.removeUserFromNode(username,token,adminUsername,adminToken);

    }


    @GetMapping("/addNewAdmin/{newAdminName}/{newAdminToken}")
    public Response addNewAdmin(@PathVariable(value = "newAdminName") String newAdminName,
                               @PathVariable(value = "newAdminToken") String newAdminToken,
                               @RequestHeader(value = "username")String adminUsername,
                               @RequestHeader(value = "token")String adminToken) throws IOException {
        return registerService.addNewAdmin(newAdminName,newAdminToken,adminUsername,adminToken);

    }



}
