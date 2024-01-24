package com.example.NodeVM.broadcast;

import com.example.NodeVM.model.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class Broadcast {
    private  final ArrayList<String> nodes =new ArrayList<>();
    private static Broadcast instance=null;
    private Broadcast() {
        nodes.add("node1");
        nodes.add("node2");
        nodes.add("node3");

    }

    public static Broadcast getInstance() {
       if (instance==null)
           instance=new Broadcast();
        return instance;
    }



    public void broadcast(String endPoint, String method, String requestBody) {
        for (String node : nodes) {
            String url = "http://" + node + ":4001/" + endPoint;
            HttpHeaders headers = new HttpHeaders();
            headers.set("username", "root");
            headers.set("token", "root123");
            headers.set("broadcast", "true");


            HttpMethod httpMethod = switch (method) {
                case "GET" -> HttpMethod.GET;
                case "DELETE" -> HttpMethod.DELETE;
                case "POST" -> HttpMethod.POST;
                default -> HttpMethod.GET;
            };

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
           restTemplate.exchange(url, httpMethod, requestEntity, Void.class);
        }


    }

    public void sendDocToNodeHasAffinity(String dbName, String collectionName, String documentJson) {

        for (String node : nodes) {
            String url = "http://" + node + ":4001/affinity/isAffinity";
            HttpHeaders headers = new HttpHeaders();
            headers.set("username", "root");
            headers.set("token", "root123");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Boolean> responseEntity =restTemplate.exchange(url, HttpMethod.GET, requestEntity, Boolean.class);
            if (responseEntity.getBody()) {
                String affinityUrl = "http://" + node + ":4001/document/addDoc/" + dbName + "/" + collectionName;
                HttpEntity<String> affinityRequestEntity = new HttpEntity<>(documentJson, headers);
                restTemplate.postForObject(affinityUrl, affinityRequestEntity, Response.class);
                break;
            }
        }
    }



}


