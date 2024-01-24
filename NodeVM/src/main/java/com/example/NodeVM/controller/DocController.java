package com.example.NodeVM.controller;
import com.example.NodeVM.model.Response;
import com.example.NodeVM.service.DocService;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
@RequestMapping("/document")
public class DocController {
    private DocService docService=new DocService();


    @PostMapping("/addDoc/{DBName}/{collectionName}")
    public Response addDoc(@PathVariable(value = "DBName") String DBName,
                           @PathVariable(value = "collectionName") String collectionName,
                           @RequestBody String document,
                           @RequestHeader(value = "username") String username,
                           @RequestHeader(value = "token") String token,
                           @RequestHeader(value = "broadcast", defaultValue = "false") boolean broadcast) throws IOException {


       return docService.addDoc(DBName,collectionName,document,username,token,broadcast);

    }

    @DeleteMapping("/deleteDoc/{DBName}/{collectionName}/{docID}")
    @ResponseBody
    public Response deleteDoc(@PathVariable(value = "DBName") String DBName,
                              @PathVariable(value = "collectionName") String collectionName,
                              @PathVariable(value = "docID") String docID,
                              @RequestHeader(value = "username") String username,
                              @RequestHeader(value = "token") String token,
                              @RequestHeader(value = "broadcast", defaultValue = "false") boolean broadcast) throws IOException {
       return docService.deleteDoc(DBName,collectionName,docID,username,token,broadcast);
    }

    @PostMapping ("/updateDoc/{DBName}/{collectionName}/{docID}/{propertyName}/{newValue}")
    @ResponseBody
    public Response updateDoc(@PathVariable(value = "DBName") String DBName,
                              @PathVariable(value = "collectionName") String collectionName,
                              @PathVariable(value = "docID") String docID,
                              @PathVariable("propertyName") String propertyName,
                              @PathVariable("newValue") Object newValue,
                              @RequestHeader(value = "username") String username,
                              @RequestHeader(value = "token") String token,
                              @RequestHeader(value = "broadcast", defaultValue = "false") boolean broadcast,
                              @RequestHeader(value = "redirectedValue", defaultValue = "same value inside affinity") String redirectedValue) throws IOException {
        return docService.updateDoc(DBName,collectionName,docID,propertyName,newValue,username,token,broadcast,redirectedValue);

    }

    @GetMapping("/getDoc/{DBName}/{collectionName}/{docID}")
    @ResponseBody
    public Response getDoc(
            @PathVariable("DBName") String DBName,
            @PathVariable("collectionName") String collectionName,
            @PathVariable("docID") String docID,
            @RequestHeader(value = "username") String username,
            @RequestHeader(value = "token") String token) throws IOException {

        return docService.getDoc(DBName,collectionName,docID,username,token);
    }

    @GetMapping("/getAllDocuments/{DBName}/{collectionName}")
    @ResponseBody
    public Response getAllDocuments(
            @PathVariable("DBName") String DBName,
            @PathVariable("collectionName") String collectionName,
            @RequestHeader(value = "username") String username,
            @RequestHeader(value = "token") String token
    ) throws IOException {
       return docService.getAllDocuments( DBName,  collectionName,  username,  token);
    }

    @GetMapping("/readingSpecificProperties/{DBName}/{collectionName}/{docID}/{propertyName}")
    @ResponseBody
    public Response readSpecificProperty(
            @PathVariable("DBName") String DBName,
            @PathVariable("collectionName") String collectionName,
            @PathVariable("docID") String docID,
            @PathVariable("propertyName") String propertyName,
            @RequestHeader(value = "username") String username,
            @RequestHeader(value = "token") String token
    ) {
        return docService.readingSpecificProperties( DBName,  collectionName,docID,propertyName , username,  token);

    }

    @GetMapping("/filterDoc/{DBName}/{collectionName}/{propertyName}/{propertyValue}")
    @ResponseBody
    public Response filterDoc(@PathVariable("DBName") String DBName,
                              @PathVariable("collectionName") String collectionName,
                              @PathVariable("propertyName") String propertyName,
                              @PathVariable("propertyValue") String propertyValue,
                              @RequestHeader(value = "username") String username,
                              @RequestHeader(value = "token") String token) throws IOException {

        return docService.filterDoc( DBName,  collectionName,propertyName,propertyValue , username,  token);

    }

}