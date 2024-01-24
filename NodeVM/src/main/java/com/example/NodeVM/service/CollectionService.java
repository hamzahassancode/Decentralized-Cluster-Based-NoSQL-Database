package com.example.NodeVM.service;

import com.example.NodeVM.broadcast.Broadcast;
//import com.example.NodeVM.model.DBSchema;
import com.example.NodeVM.model.Response;
import com.example.NodeVM.repo.CollectionRepo;

import java.io.File;
import java.io.IOException;

import static com.example.NodeVM.DATA.GetPath.DATABASE_PATH;
import static com.example.NodeVM.model.Response.Status.*;
import static com.example.NodeVM.model.Response.Status.SUCCESS;

public class CollectionService {

    private final Broadcast broadcastToNodes=Broadcast.getInstance();
    private final ValidatorService validatorService=new ValidatorService();
    private CollectionRepo collectionRepo=new CollectionRepo();

    public Response createCollection( String DBName, String collectionName, String schema, String username, String token, boolean broadcast) throws IOException {

        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't create collection, NOT valid user!");
        }

        File collectionFile = new File(DATABASE_PATH + DBName + "/" + collectionName + ".json");
        if (collectionFile.exists()) {
            return new Response(BAD_REQUEST, "Collection already exists.");
        }

        if (!broadcast){
            String endPoint = "collection/createCollection/" + DBName + "/" + collectionName;
             broadcastToNodes.broadcast(endPoint,"POST",schema);
        }else {
            // Create the collection file
            return collectionRepo.createCollection(DBName,collectionName,schema);
        }
        return new Response(SUCCESS, "Collection created successfully.");

    }

    public Response deleteCollection( String DBName, String collectionName , String username, String token, boolean broadcast) throws IOException {
        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't delete collection, NOT valid user!");
        }

        File collectionFile = new File(DATABASE_PATH + DBName + "/" + collectionName + ".json");
        if (!collectionFile.exists()) {
            return new Response(BAD_REQUEST, "Collection not found.");
        }

        if (!broadcast){
            String endPoint = "collection/deleteCollection/" + DBName + "/" + collectionName;
            broadcastToNodes.broadcast(endPoint,"DELETE","");
        }else {
           return collectionRepo.deleteCollection(DBName,collectionName);
        }
        return new Response(SUCCESS, "Collection DELETE successfully.");
    }

    public Response showCollections(String DBName, String username, String token) throws IOException {
        DBName = DBName.toLowerCase();
        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't create collection, NOT valid user!");
        }
        return new Response(SUCCESS, collectionRepo.showAllCollections(DBName));

    }

}
