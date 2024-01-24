package com.example.NodeVM.repo;


import com.example.NodeVM.model.Response;

public interface DatabaseDAO {

    Response createDatabase(String DBName);
    Response deleteDatabase(String DBName);

    String showAllDatabases();
}
