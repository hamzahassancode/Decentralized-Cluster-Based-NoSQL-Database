package com.example.NodeVM.repo;


import org.json.JSONArray;
import org.json.JSONObject;

public interface DocumentDAO {

    void addDoc(String DBName, String collectionName, String documentJson);

    boolean deleteDoc(String DBName, String collectionName, String docID);

    void updateDoc(String DBName, String collectionName, String docID, JSONObject newObj);

    JSONArray filteredDoc(String DBName, String collectionName, String propertyName, String propertyValue);

    String readingSpecificProperty(JSONObject jsonObject, String PropertyName);

    String getAllDocuments(String DBName, String collectionName);

    JSONObject getDocument(String DBName, String collectionName, String id);


}
