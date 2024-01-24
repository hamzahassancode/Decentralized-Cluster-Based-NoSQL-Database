package com.example.NodeVM.repo;

import com.example.NodeVM.cache.Cache;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.example.NodeVM.DATA.GetPath.AFFINITY_PATH;
import static com.example.NodeVM.repo.RegisterRepo.convertFileToString;

public class AffinityRepo {
    private Cache<String, String> cache = new Cache();

    public String getNodeAffinityToSpecificDoc(String docID){
        String nodeName =  cache.get(docID);
        if (nodeName != null) {
            System.out.println("getNodeAffinityToSpecificDoc GET FROM CACHE");

            return nodeName;
        }
        JSONArray jsonArray = new JSONArray(convertFileToString(AFFINITY_PATH));
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            if (jsonObject.get("docID").equals(docID)) {
                return (String) jsonObject.get("affinityNode");
            }
        }
        return "can't get node affinity to this document";

    }


    public void associateDocumentWithAffinityNode(String docID, String affinityNode){
        cache.put(docID,affinityNode);
        JSONArray jsonArray = new JSONArray(convertFileToString(AFFINITY_PATH));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("docID", docID);
        jsonObject.put("affinityNode", affinityNode);
        jsonArray.put(jsonObject);
        RegisterRepo.writeJSONArrayToFile(jsonArray,AFFINITY_PATH);


    }

    public void removeNodeAffinityFromFile(String docID){
        cache.remove(docID);

        JSONArray jsonArray = new JSONArray(convertFileToString(AFFINITY_PATH));
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject= jsonArray.getJSONObject(i);
            if (jsonObject.get("docID").equals(docID)) {
                jsonArray.remove(i);
                break;
            }
        }
        RegisterRepo.writeJSONArrayToFile(jsonArray,AFFINITY_PATH);
    }
    public String getAllDocToNodeAffinity() {
        File collectionFile=new File(AFFINITY_PATH);
        try {
            return FileUtils.readFileToString(collectionFile, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
