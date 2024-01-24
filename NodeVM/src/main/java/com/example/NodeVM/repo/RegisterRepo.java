package com.example.NodeVM.repo;

import com.example.NodeVM.cache.Cache;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class RegisterRepo {

    private static final Object lock = new Object();
    private Cache<String, String> cache=new Cache<>();

    public  boolean isAuthenticated(String username, String token, String collectionPathFile)  {
        //try to get the user from cache
        String name = cache.get(token);
        if (name != null && name.equals(username)) {
            System.out.println("isAuthenticated GET FROM CACHE");
            return true;
        }

        //try to find the user in file
        JSONArray jsonArray = new JSONArray(convertFileToString(collectionPathFile));
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if( jsonObject.get("username").equals(username) && jsonObject.get("token").equals(token)){
                return true;
            }
        }
        return false;
    }


    public void addAuthenticate(String username, String token, String collectionPath){
        if (username == null || token == null)
            throw new IllegalArgumentException("Username or Token is null");

        // adding to the cache.
        cache.put(token, username);
        // adding to file
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("username",username);
        jsonObject.put("token",token);

        JSONArray jsonArray = new JSONArray(convertFileToString(collectionPath));
        jsonArray.put(jsonObject);

        RegisterRepo.writeJSONArrayToFile(jsonArray,collectionPath);
    }

    public void removeAuthenticate(String username,String token, String collectionPath) throws IOException {
        if (username == null || token == null)
            throw new IllegalArgumentException("username or token is null");

        // remove from the cache
        cache.remove(token);

        // remove from the file
        JSONArray jsonArray = new JSONArray(convertFileToString(collectionPath));
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if( jsonObject.get("username").equals(username) && jsonObject.get("token").equals(token)){
                jsonArray.remove(i);
                break;
            }
        }
        writeJSONArrayToFile(jsonArray,collectionPath);

    }

    public static void writeJSONArrayToFile(JSONArray jsonArray,String FILE_PATH) {
        File file = new File(FILE_PATH);
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            synchronized (lock) {
                fileWriter.write(jsonArray.toString());
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertFileToString(String Path) {
        File file=new File(Path);
        try {
            return new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
