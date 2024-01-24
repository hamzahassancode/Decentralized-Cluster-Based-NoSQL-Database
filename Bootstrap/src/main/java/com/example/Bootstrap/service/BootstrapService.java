package com.example.Bootstrap.service;
import com.example.Bootstrap.repo.BootstrapRepo;
import org.springframework.stereotype.Service;

@Service
public class BootstrapService {

    private BootstrapRepo bootstrapRepo= BootstrapRepo.getInstance();

    private BootstrapService(){

    }

    public String registerNewUser(String username){
        return bootstrapRepo.addNewUser(username);
    }
    public String getAllUsers() {
        return bootstrapRepo.allUsers();
    }

    public void removeUser(String token) {
        bootstrapRepo.removeUser(token);
    }


}
