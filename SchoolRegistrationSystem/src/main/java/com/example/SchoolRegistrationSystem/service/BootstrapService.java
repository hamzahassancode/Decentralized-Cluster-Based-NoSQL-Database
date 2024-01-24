package com.example.SchoolRegistrationSystem.service;

import com.example.SchoolRegistrationSystem.repo.BootstrapRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ui.Model;

import java.util.HashMap;

import static com.example.SchoolRegistrationSystem.service.NodeService.assignNodeToToken;

public class BootstrapService {
    private static BootstrapService instance;
    private BootstrapRepo bootstrapRepo=new BootstrapRepo();
private BootstrapService(){

}
public static BootstrapService getInstance(){
    if(instance==null){
        instance=new BootstrapService();
    }
    return instance;

}
    public void prepareEmployeeData(String username, Model model){

        JSONObject jsonObject = new JSONObject(bootstrapRepo.registerNewEmployee(username));
        String token = jsonObject.getString("token");
        int nodePort = jsonObject.getInt("nodePort");

        model.addAttribute("username", username);
        model.addAttribute("token", token);
        model.addAttribute("nodePort",nodePort-4000);
        assignNodeToToken(token, nodePort);
    }

    public void removeEmployeeData(String token){
        bootstrapRepo.removeEmployee(token);
    }
    public void getAllEmployees(Model model){
        JSONArray allEmployees=bootstrapRepo.getAllEmployees();
        model.addAttribute("allEmployees",allEmployees);

    }


}
