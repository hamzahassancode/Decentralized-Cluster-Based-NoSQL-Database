package com.example.NodeVM.service;

import com.example.NodeVM.model.Response;
import com.example.NodeVM.repo.RegisterRepo;
import org.springframework.stereotype.Service;
import java.io.IOException;
import static com.example.NodeVM.DATA.GetPath.AUTHENTICATION_PATH;
import static com.example.NodeVM.model.Response.Status.INTERNAL_ERROR;
import static com.example.NodeVM.model.Response.Status.SUCCESS;

@Service
public class RegisterService {
    private RegisterRepo registerRepo=new RegisterRepo();
    private ValidatorService validatorService=new ValidatorService();
    public RegisterService(){
    }
    public Response addNewUserToNode( String userUsername, String userToken, String adminUsername, String adminToken) throws IOException {
        if (!validatorService.isAdmin(adminUsername,adminToken)){
            return new Response(INTERNAL_ERROR,"not valid admin!");
        }
        registerRepo.addAuthenticate( userUsername, userToken, AUTHENTICATION_PATH+ "UsersData.json");//add NEW user to file UsersData.json
        return new Response(SUCCESS,"NEW User added successfully tO node !");

    }
    public Response removeUserFromNode( String userUsername, String userToken, String adminUsername, String adminToken) throws IOException {

        if (!validatorService.isAdmin(adminUsername, adminToken)) {
            return new Response(INTERNAL_ERROR, "Not valid admin!");
        }
        if (!validatorService.isValidUser(userUsername, userToken)) {
            return new Response(INTERNAL_ERROR, "User is not registered !");
        }
        registerRepo.removeAuthenticate( userUsername, userToken, AUTHENTICATION_PATH+ "UsersData.json");
        return new Response(SUCCESS, "User removed successfully!");
    }
    public Response addNewAdmin( String newAdminName, String newAdminToken, String adminUsername, String adminToken) throws IOException {
        if (!validatorService.isAdmin(adminUsername,adminToken)){
            return new Response(INTERNAL_ERROR,"not valid admin!");
        }
        registerRepo.addAuthenticate( newAdminName, newAdminToken, AUTHENTICATION_PATH+"AdminData.json");//add new admin to file AdminData.json
        return new Response(SUCCESS,"NEW admin added successfully !");

    }

    public void removeAdmin(String username,String token) throws IOException {

        registerRepo.removeAuthenticate( username, token, AUTHENTICATION_PATH+ "AdminData.json");
    }
}
