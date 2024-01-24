package com.example.Bootstrap.repo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class BootstrapRepo {
    private static final String USERSDATAPath = "src/main/java/com/example/Bootstrap/usersDATA/usersDATA.json";
    private int nodeIndex = 0;
    private HttpHeaders headers;
    private RestTemplate restTemplate=new RestTemplate();
    private ArrayList<String> nodes;

    private static BootstrapRepo instance;
    private File usersDataFile;

    private BootstrapRepo() {
        usersDataFile = new File(USERSDATAPath);

        nodes=new ArrayList<>();
        for (int i=1;i<=3;i++){
            nodes.add("node"+i);
        }
        headers=new HttpHeaders();
        headers.set("username","root");
        headers.set("token","root123");
    }

    public static BootstrapRepo getInstance() {
        if (instance == null)
            instance = new BootstrapRepo();
        return instance;
    }
    public int getPort(String nodeName){
        return switch (nodeName) {
            case "node1" -> 4001;
            case "node2" -> 4002;
            default -> 4003;
        };

    }

    public String getNodeByLoadBalance(){
        int index = nodeIndex++ % nodes.size();
        return nodes.get(index);
    }
    public void setUpNodes() {
        for (int i = 1; i <= 3; i++) {
            String nodeName="node" + i;
            String url = "http://" + nodeName + ":4001/affinity/setCurrentNode/" + nodeName;
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.GET, requestEntity, Void.class);
        }
        String url = "http://node1:4001/affinity/setAffinityNode";
        HttpEntity<String> requestEntity = new HttpEntity<>( headers);
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, Void.class);
    }
    public void sendUserInfoToNode(String username, String token, String node) {
        String url = "http://" + node + ":4001/register/addNewUserToNode/" + username + "/" + token;
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, Void.class);
    }

    public void removeUserFromNode(String username, String token, String nodeName) {
        String url = "http://" + nodeName + ":4001/register/removeUserFromNode/" + username + "/" + token;
        HttpEntity<String> requestEntity = new HttpEntity<>( headers);
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
    }
    public String addNewUser(String username){

        String token= UUID.randomUUID().toString();
        String node= getNodeByLoadBalance();

        JSONObject userData = new JSONObject();
        userData.put("username",username);
        userData.put("token",token);
        userData.put("nodePort",getPort(node));

        sendUserInfoToNode(username,token,node);

        registerNewUserToFile(username, token, node);

        return userData.toString();
    }



    public void registerNewUserToFile(String userName, String token, String nodeName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userName);
        jsonObject.put("token", token);
        jsonObject.put("nodeName", nodeName);

        String content = convertFileToString(usersDataFile);
        JSONArray jsonArray = new JSONArray(content);
        jsonArray.put(jsonObject);

        try {
            Files.write(Paths.get(USERSDATAPath), jsonArray.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String allUsers() {
        return convertFileToString(usersDataFile);
    }

    public void removeUser(String token) {
        JSONArray jsonArray = new JSONArray(convertFileToString(usersDataFile));

        for (int i =0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String currentToken = jsonObject.getString("token");
            if (currentToken.equals(token)) {
                String userName = jsonObject.getString("username");
                String nodeName = jsonObject.getString("nodeName");

                removeUserFromNode(userName, token, nodeName);

                jsonArray.remove(i);
                break;
            }
        }

        // writing the new content to the tokens.json file
        try {
            Files.write(Paths.get(USERSDATAPath), jsonArray.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void removeAllUsers() {
        try {
            Files.write(Paths.get(USERSDATAPath), "[]".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static String convertFileToString(File file) {
        try {
            return new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    public void checkUsersData(){
//        JSONArray jsonArray=new JSONArray(convertFileToString(usersDataFile));
//
//        if (jsonArray==null)
//            return;
//
//        for (int i =0; i < jsonArray.length(); i++) {
//            JSONObject user = jsonArray.getJSONObject(i);
//            String token = user.getString("token");
//            String username = user.getString("username");
//            String nodeName = user.getString("nodeName");
//            sendUserInfoToNode(username,token,nodeName);
//
//
//
//        }
//
//
//    }



}
