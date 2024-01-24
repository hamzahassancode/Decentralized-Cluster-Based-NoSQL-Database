package com.example.NodeVM.service;


import com.example.NodeVM.repo.RegisterRepo;
import org.springframework.stereotype.Service;
import java.io.File;

import static com.example.NodeVM.DATA.GetPath.*;

@Service
public class ValidatorService {
private RegisterRepo registerRepo=new RegisterRepo();
    public ValidatorService() {
    }

    public boolean isValidUser(String username, String token) {
        if (username == null || token == null)
            throw new IllegalArgumentException("username or token is null");

        return registerRepo.isAuthenticated(username,token, AUTHENTICATION_PATH +"UsersData.json");
    }



    public boolean isAdmin(String adminUsername, String adminToken){
        if (adminUsername == null || adminToken == null)
            throw new IllegalArgumentException("username or token is null");

        return registerRepo.isAuthenticated(adminUsername,adminToken, AUTHENTICATION_PATH + "AdminData.json");
    }

    public boolean isDatabaseExists(String DBName) {
        File dbDirectory = new File(DATABASE_PATH + DBName);
        return dbDirectory.exists() && dbDirectory.isDirectory();
    }
    public boolean isCollectionExists(String DBName,String collectionName) {
        File collectionFile = new File(DATABASE_PATH + DBName + "/" + collectionName + ".json");
        return collectionFile.exists() && collectionFile.isFile();
    }



}
