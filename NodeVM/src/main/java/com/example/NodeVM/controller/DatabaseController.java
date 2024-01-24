package com.example.NodeVM.controller;

import com.example.NodeVM.model.Response;
import com.example.NodeVM.service.DatabaseService;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
@RequestMapping("/database")
public class DatabaseController {
    private DatabaseService databaseService=new DatabaseService();

    @PostMapping("/createDB/{DBName}")
    public Response createDB(@PathVariable("DBName") String DBName,
                             @RequestHeader(value = "broadcast",defaultValue = "false") boolean broadcast,
                             @RequestHeader (value = "username") String username,
                             @RequestHeader(value = "token") String token) throws IOException {
    return databaseService.createDatabase(DBName,broadcast,username,token);
    }

    @DeleteMapping("/deleteDB/{DBName}")
    public Response deleteDB(@PathVariable String DBName,
                             @RequestHeader (value = "username") String username,
                             @RequestHeader(value = "token") String token,
                             @RequestHeader(value = "broadcast",defaultValue = "false") boolean broadcast) throws IOException {

    return databaseService.deleteDatabase(DBName,broadcast,username,token);
    }

    @GetMapping("/showAllDatabases")
    public Response showAllDatabases(@RequestHeader(value = "username") String username,
                                     @RequestHeader(value = "token") String token) throws IOException {
       return databaseService.showAllDatabases(username,token);
}}
