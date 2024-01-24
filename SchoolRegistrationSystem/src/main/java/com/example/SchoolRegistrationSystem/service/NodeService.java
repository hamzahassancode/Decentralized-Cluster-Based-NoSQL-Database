package com.example.SchoolRegistrationSystem.service;

import com.example.SchoolRegistrationSystem.repo.NodeRepo;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ui.Model;

import java.util.HashMap;

public class NodeService {
    private NodeRepo nodeRepo=new NodeRepo();

    private static NodeService instance;
    private static HashMap<String, Integer> tokenNode = new HashMap<>();


    private NodeService(){}
    public static NodeService getInstance(){
        if(instance==null){
            instance=new NodeService();
        }
        return instance;
    }
    public void buildDatabaseSchema() {
        nodeRepo.buildDatabaseSchema();
    }
    public boolean isAdmin(String username, String token, HttpSession httpSession, Model model) {
        if (nodeRepo.isAdmin(username, token)) {

            model.addAttribute("username", username);
            model.addAttribute("token", token);

            httpSession.setAttribute("username", username);
            httpSession.setAttribute("token", token);

            return true;
        } else {
            return false;
        }
    }

    public static void assignNodeToToken(String token, int nodePort) {
        tokenNode.put(token, nodePort);
    }
    public static String getNodePort(String token) {
        return String.valueOf(tokenNode.get(token));

    }
    /**
     *
     * **/
    public void signInEmployee(){

    }
    public boolean isEmployee(String username, String token,HttpSession httpSession, Model model) {

        model.addAttribute("username", username);
        model.addAttribute("token", token);

        if (nodeRepo.isEmployee(username, token)) {

            int nodePort = Integer.parseInt(getNodePort(token));

            model.addAttribute("username", username);
            model.addAttribute("token", token);
            model.addAttribute("nodePort", nodePort);

            httpSession.setAttribute("username", username);
            httpSession.setAttribute("token", token);
            httpSession.setAttribute("nodePort", nodePort);
            httpSession.setAttribute("content-type","application/json");

            return true;
        } else {

            return false;
        }
    }
    public void addNewStudent( String studentName,  String studentPhone,
                               String studentAddress,  int sectionNumber,
                               HttpSession httpSession){
        JSONObject studentData = new JSONObject();
        studentData.put("name", studentName);
        studentData.put("phone", studentPhone);
        studentData.put("address", studentAddress);
        studentData.put("sectionNumber", sectionNumber);
        nodeRepo.addNewDoc(studentData,"students",httpSession);
    }

   public void showAllStudentData(HttpSession httpSession, Model model){
        String response =nodeRepo.showAllStudentData(httpSession);
       JSONArray AllStudentData=new JSONArray(new JSONObject(response).get("message").toString());
       model.addAttribute("AllStudentData", AllStudentData);
   }
    public void getById(String ID,HttpSession httpSession, Model model){
        String studentData=nodeRepo.getById(ID,httpSession);
        JSONObject jsonObjectResponse=new JSONObject(studentData);
        String message=jsonObjectResponse.getString("message");

        if (message.equals("Could Not Find Any document")){
            model.addAttribute("CouldNotFindAnyStudent", "Could Not Find Any Student");
        }else {
            JSONObject AllStudentData = new JSONObject(message);
            model.addAttribute("AllStudentData", AllStudentData);
        }

    }
    public void filtered(String propertyName, String propertyValue, HttpSession httpSession, Model model){
        String studentData=nodeRepo.filtered(propertyName,propertyValue,httpSession);
        JSONObject jsonObjectResponse=new JSONObject(studentData);
        String message=jsonObjectResponse.getString("message");

        if (message.equals("Could Not Find Any document")){
            model.addAttribute("CouldNotFindAnyStudent", "Could Not Find Any Student");
        }else {
            JSONArray AllStudentData = new JSONArray(message);
            model.addAttribute("AllStudentData", AllStudentData);
        }

    }
    public Boolean removeStudent(String ID,HttpSession httpSession){
        String studentData=nodeRepo.deleteUser(ID,httpSession);
        JSONObject jsonObjectResponse=new JSONObject(studentData);
        String message=jsonObjectResponse.getString("message");
        return !message.equals("Can't find document");

    }


    public void update(String newValue, HttpSession httpSession ) {
        nodeRepo.update(newValue,httpSession);
    }
    public void readingSpecificProperties(String propertyName,String ID,HttpSession httpSession, Model model) {

        String responseMessage=nodeRepo.readingSpecificProperties(propertyName,ID,httpSession);
        JSONObject jsonObjectResponse=new JSONObject(responseMessage);
        String message=jsonObjectResponse.getString("message");
        model.addAttribute("phone",message);
    }

    public void addNewInstructor( String instructorName, String subjectName, HttpSession httpSession){
        JSONObject studentData = new JSONObject();
        studentData.put("instructorName", instructorName);
        studentData.put("subjectName", subjectName);
        nodeRepo.addNewDoc(studentData,"instructors",httpSession);
    }
    public void showAllInstructors(HttpSession httpSession, Model model){
        String response =nodeRepo.showAllDocInCollection("instructors",httpSession);
        JSONArray allInstructor=new JSONArray(new JSONObject(response).get("message").toString());
        model.addAttribute("allInstructor", allInstructor);
    }
    public void showAllAffinityData(HttpSession httpSession, Model model){
        String response =nodeRepo.showAllDataAffinity(httpSession);
        JSONArray AffinityData=new JSONArray(new JSONObject(response).get("message").toString());
        model.addAttribute("AffinityData", AffinityData);
    }

}
