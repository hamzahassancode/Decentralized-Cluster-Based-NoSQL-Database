package com.example.SchoolRegistrationSystem.repo;

import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.example.SchoolRegistrationSystem.service.NodeService.getNodePort;

public class NodeRepo {
    private RestTemplate restTemplate = new RestTemplate();
    public void buildDatabaseSchema() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", "root");
        headers.set("token", "root123");
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String createDatabaseURL = "http://localhost:4001/database/createDB/RegistrationSystem";
        restTemplate.exchange(createDatabaseURL, HttpMethod.POST, requestEntity, String.class);

        try {
            String studentsFilePath = "src/main/java/com/example/SchoolRegistrationSystem/schema/studentsSchema.json";
            String studentsSchema = new String(Files.readAllBytes(Paths.get(studentsFilePath)));
            String createCollectionStudentsURL = "http://localhost:4001/collection/createCollection/RegistrationSystem/students";
            HttpEntity<String> requestEntityStudent = new HttpEntity<>(studentsSchema, headers);
            restTemplate.exchange(createCollectionStudentsURL, HttpMethod.POST, requestEntityStudent, String.class);

            String instructorFilePath = "src/main/java/com/example/SchoolRegistrationSystem/schema/instructorSchema.json";
            String instructorSchema = new String(Files.readAllBytes(Paths.get(instructorFilePath)));
            String createCollectionInstructorURL = "http://localhost:4001/collection/createCollection/RegistrationSystem/instructors";
            HttpEntity<String> requestEntityInstructor = new HttpEntity<>(instructorSchema, headers);
            restTemplate.exchange(createCollectionInstructorURL, HttpMethod.POST, requestEntityInstructor, String.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isAdmin(String username, String token) {
        String url = "http://localhost:" + 4001 + "/validation/isAdmin/" + username + "/" + token;
        return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
    }
    public boolean isEmployee(String username, String token) {
        if(!"null".equals(getNodePort(token))) {
            int nodePort=Integer.parseInt(getNodePort(token));
            String url = "http://localhost:" + nodePort + "/validation/isValidUser/" + username + "/" + token;
            return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
        }
        else return false;
    }

    public void addNewDoc(JSONObject data, String collectionName, HttpSession httpSession) {
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url = "http://localhost:" + nodePort + "/document/addDoc/RegistrationSystem/"+collectionName;
        HttpEntity<String> requestEntity =getRequestEntity(data.toString(),httpSession);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }


    public String showAllStudentData(HttpSession httpSession){
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url="http://localhost:"+nodePort+"/document/getAllDocuments/RegistrationSystem/students";
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }

    public String getById(String ID,HttpSession httpSession){
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url="http://localhost:"+nodePort+"/document/getDoc/RegistrationSystem/students/"+ID;
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }
    public String filtered(String propertyName,String propertyValue,HttpSession httpSession){
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url="http://localhost:"+nodePort+"/document/filterDoc/RegistrationSystem/students/"+propertyName+"/"+propertyValue;
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }

    public String deleteUser(String ID, HttpSession httpSession) {
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url = "http://localhost:" + nodePort + "/document/deleteDoc/RegistrationSystem/students/" + ID;
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class).getBody();
    }

    public void update(String newValue, HttpSession httpSession) {
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String docId = httpSession.getAttribute("docId").toString();
        String propertyName = httpSession.getAttribute("propertyName").toString();;

        String url = "http://localhost:" + nodePort + "/document/updateDoc/RegistrationSystem/students/" + docId+"/"+propertyName+"/"+newValue;
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }
    public String readingSpecificProperties(String propertyName,String ID,HttpSession httpSession){
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url="http://localhost:"+nodePort+"/document/readingSpecificProperties/RegistrationSystem/students/"+ID+"/"+propertyName;
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }


    public HttpEntity<String> getRequestEntity(String data,HttpSession httpSession){
        String username = httpSession.getAttribute("username").toString();
        String token = httpSession.getAttribute("token").toString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("token", token);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(data,headers);
        return requestEntity;
    }

    public String showAllDocInCollection(String collectionName, HttpSession httpSession){
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url="http://localhost:"+nodePort+"/document/getAllDocuments/RegistrationSystem/"+collectionName;
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }
    public String showAllDataAffinity(HttpSession httpSession){
        String nodePort = httpSession.getAttribute("nodePort").toString();
        String url="http://localhost:"+nodePort+"/affinity/getAllDocToNodeAffinity";
        HttpEntity<String> requestEntity = getRequestEntity("",httpSession);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
    }
}
