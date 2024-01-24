package com.example.NodeVM.controller;

import com.example.NodeVM.model.Response;
import com.example.NodeVM.service.CollectionService;

import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    private CollectionService collectionService=new CollectionService();

    @PostMapping("createCollection/{DBName}/{collectionName}")
    @ResponseBody
    public Response createCollection(@PathVariable("DBName") String DBName,
                                  @PathVariable("collectionName") String collectionName,
                                  @RequestBody String schema,
                                  @RequestHeader(value = "username") String username,
                                  @RequestHeader(value = "token") String token,
                                  @RequestHeader(value = "broadcast", defaultValue = "false") boolean broadcast) throws IOException {
       return collectionService.createCollection(DBName,collectionName,schema,username,token,broadcast);
    }

    @DeleteMapping("deleteCollection/{DBName}/{collectionName}")
    @ResponseBody
    public Response deleteCollection(@PathVariable("DBName") String DBName,
                                  @PathVariable("collectionName") String collectionName,
                                  @RequestHeader(value = "username") String username,
                                  @RequestHeader(value = "token") String token,
                                  @RequestHeader(value = "broadcast", defaultValue = "false") boolean broadcast) throws IOException {
        return collectionService.deleteCollection(DBName,collectionName,username,token,broadcast);
    }

    @GetMapping("showAllCollections/{DBName}")
    public Response showCollections(@PathVariable("DBName") String DBName,
                                    @RequestHeader(value = "username") String username,
                                    @RequestHeader(value = "token") String token) throws IOException {
        return collectionService.showCollections(DBName,username,token);

    }
}
