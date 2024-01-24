package com.example.NodeVM.repo;

import com.example.NodeVM.model.Response;

public interface CollectionDAO {

    Response createCollection(String dbName, String collectionName, String schema);

    Response deleteCollection(String DBName, String collectionName);

    String showAllCollections(String DBName);
}
