package com.example.NodeVM.service;

import com.example.NodeVM.broadcast.Broadcast;
import com.example.NodeVM.model.Response;
import com.example.NodeVM.repo.DatabaseRepo;

import java.io.IOException;

import static com.example.NodeVM.model.Response.Status.*;

public class DatabaseService {

    private Broadcast broadcastToNodes=Broadcast.getInstance();

    private ValidatorService validatorService=new ValidatorService();
    private DatabaseRepo databaseRepo=new DatabaseRepo();

    public Response createDatabase( String DBName, boolean broadcast, String username, String token) throws IOException {
        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't create database, NOT valid user!");
        }

        DBName = DBName.toLowerCase();

        if (validatorService.isDatabaseExists(DBName)) {
            return new Response(BAD_REQUEST, "Database is already exists");
        }

        if (!broadcast){
            String endPoint = "database/createDB/" + DBName;
            broadcastToNodes.broadcast(endPoint,"POST","");
        }else {
            // Create the database directory
           return databaseRepo.createDatabase(DBName);
        }
        return new Response(SUCCESS, "Database "+DBName+" created successfully.");
    }

    public Response deleteDatabase( String DBName, boolean broadcast, String username, String token) throws IOException {
        DBName = DBName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "NOT valid user!");
        }

        if (broadcast) {
            return databaseRepo.deleteDatabase(DBName);
        } else {
            String endPoint = "database/deleteDB/" + DBName;
            broadcastToNodes.broadcast(endPoint,"DELETE", "");
        }
        return new Response(SUCCESS, "Database "+DBName+" deleted successfully");
    }
    public Response showAllDatabases(  String username, String token) throws IOException {
        if (validatorService.isValidUser(username, token)) {
            return new Response(SUCCESS,"ALL DATABASES : "+ showAllDatabases());
        }
        return new Response(NOT_FOUND, "not valid user ");
    }
    public String showAllDatabases() {
       return databaseRepo.showAllDatabases();
    }
}
