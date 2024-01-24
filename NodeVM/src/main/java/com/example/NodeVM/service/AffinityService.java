package com.example.NodeVM.service;

import com.example.NodeVM.model.Response;
import com.example.NodeVM.repo.AffinityRepo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.NodeVM.model.Response.Status.*;


@Service
public class AffinityService {
    private static AffinityService instance;

    private boolean hasAffinity =false;

private AffinityRepo affinityRepo=new AffinityRepo();

    private String currentNode;

    private AffinityService() {

    }
public static AffinityService getInstance(){
        if (instance==null)
            return instance=new AffinityService();
        return instance;
}
    public void setAffinityNode(){
        hasAffinity =true;

    }
    public void clearAffinityNode(){
        hasAffinity =false;
    }
    public boolean isAffinityEnabled() {
        return hasAffinity;
    }

    public void associateDocumentWithAffinityNode(String docID, String affinityNode) throws IOException {
        affinityRepo.associateDocumentWithAffinityNode(docID,affinityNode);
    }

    public void removeNodeAffinity(String docID){
       affinityRepo.removeNodeAffinityFromFile(docID);
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public String getNodeAffinityToSpecificDoc(String docID){
        return affinityRepo.getNodeAffinityToSpecificDoc(docID);
    }

    public void cycleAffinityNode() {
        clearAffinityNode();
        Map<String, String> nodeMapping = new HashMap<>();
        nodeMapping.put("node1", "node2");
        nodeMapping.put("node2", "node3");
        nodeMapping.put("node3", "node1");

        String currentNode = getCurrentNode();
        String nextNode = nodeMapping.get(currentNode);

        String url = "http://" + nextNode + ":4001/affinity/setAffinityNode";
        HttpEntity<String> requestEntity = new HttpEntity<>("");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, Void.class);
    }
    public Response getAllDocToNodeAffinity() {

        String data = affinityRepo.getAllDocToNodeAffinity();
        if (data!=null)
            return new  Response(SUCCESS,data);
        else
            return new  Response(NOT_FOUND,"");

    }



}
