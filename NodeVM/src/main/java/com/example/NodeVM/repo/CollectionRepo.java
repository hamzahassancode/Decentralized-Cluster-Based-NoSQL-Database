package com.example.NodeVM.repo;

import com.example.NodeVM.indexing.Indexing;
import com.example.NodeVM.model.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.NodeVM.DATA.GetPath.DATABASE_PATH;
import static com.example.NodeVM.DATA.GetPath.DATABASE_SCHEMAS_PATH;
import static com.example.NodeVM.model.Response.Status.*;

public class CollectionRepo implements CollectionDAO {

    private final Object lock = new Object();
   private Indexing indexing = Indexing.getInstance();
    public CollectionRepo(){

    }

    @Override
    public Response createCollection(String dbName, String collectionName, String schema) {
        synchronized (lock) {
            try {
                File collectionFile = new File(DATABASE_PATH + dbName + "/" + collectionName + ".json");
                File schemaFile = new File(DATABASE_SCHEMAS_PATH + dbName + "/" + collectionName + ".json");

                if (collectionFile.createNewFile() && schemaFile.createNewFile()) {
                    try (BufferedWriter collectionWriter = new BufferedWriter(new FileWriter(collectionFile));
                         BufferedWriter schemaWriter = new BufferedWriter(new FileWriter(schemaFile))) {

                        // Write [] in collectionWriter to be ready to be JSONArray to can save the JSONObject(document)
                        collectionWriter.write("[]");

                        // Write the schema to the schema file
                        schemaWriter.write(schema);
                    }

                    return new Response(SUCCESS, "Collection created successfully.");
                } else {
                    throw new IOException("Failed to create one or both of the files.");
                }
            } catch (IOException e) {
                return new Response(INTERNAL_ERROR, "Error when trying to create a collection.");
            }
        }
    }
    @Override
    public Response deleteCollection(String DBName, String collectionName) {
        try {
            File collectionFile = new File(DATABASE_PATH + DBName + "/" + collectionName + ".json");
            File schemaFile = new File(DATABASE_SCHEMAS_PATH+ DBName + "/" + collectionName + ".json");
         indexing.removeCollectionFromIndex(DBName,collectionName);
            synchronized (lock) {
                collectionFile.delete();
                schemaFile.delete();
            }
            return new Response(SUCCESS, "Collection DELETE successfully.");
        } catch (Exception e) {
            return new Response(BAD_REQUEST, "Error when deleting collection");

        }
    }


    @Override
    public  String showAllCollections(String DBName ) {

        File dbDirectory = new File(DATABASE_PATH + DBName);

        if (dbDirectory.exists() && dbDirectory.isDirectory()) {
            File[] files = dbDirectory.listFiles();
            StringBuilder stringBuilder = new StringBuilder();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        stringBuilder.append(file.getName()).append("  ");
                    }
                }
            }else {
                return "";
            }
            return stringBuilder.toString();
        } else {
            return "Directory does not exist.";
        }
    }




}

