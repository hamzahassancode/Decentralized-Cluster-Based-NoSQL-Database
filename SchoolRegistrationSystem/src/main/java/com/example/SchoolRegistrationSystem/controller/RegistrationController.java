package com.example.SchoolRegistrationSystem.controller;

import com.example.SchoolRegistrationSystem.service.NodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.SchoolRegistrationSystem.service.BootstrapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    private NodeService nodeService = NodeService.getInstance();

    private BootstrapService bootstrapService = BootstrapService.getInstance();

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("registerNewEmployee")
    public String registerNewEmployee() {
        return "register-Employee";
    }

    @PostMapping("returnEmployeeData")
    public String showEmployeeData(@RequestParam String username, Model model) {
        bootstrapService.prepareEmployeeData(username, model);
        return "return-Employee-Data";
    }

    @GetMapping("enterAsAdmin")
    public String enterAsAdmin() {
        return "enter-as-admin";
    }

    @GetMapping("removeEmployeePage")
    public String removeUser() {
        return "remove-employee";
    }

    @PostMapping("removeEmployee")
    public String removeEmployeeData(@RequestParam String token) {
        bootstrapService.removeEmployeeData(token);
        return "success";
    }

    @GetMapping("allEmployees")
    public String allEmployees(Model model) {
        bootstrapService.getAllEmployees(model);
        return "show-all-employees-data";
    }

    @PostMapping("isAdmin")
    public String isAdmin(@RequestParam String username, @RequestParam String token, Model model, HttpSession httpSession) {
        if (nodeService.isAdmin(username, token, httpSession, model)) {
            return "admin-page";
        } else return "failed";

    }

    /**
     * sign in employee
     **/
    @GetMapping("firstPage")
    public String firstPage() {
        return "system";
    }

    @GetMapping("signInEmployeePage")
    public String signInEmployeePage() {
        return "sign-in-employee-page";
    }
    @PostMapping("system")
    public String isEmployee(@RequestParam String username, @RequestParam String token, HttpSession httpSession, Model model) {
        if (nodeService.isEmployee(username, token, httpSession, model)) {
            return "system";
        } else {
            return "failed";
        }

    }

    //add new student
    @GetMapping("addNewStudent")
    public String RegisterNewStudent() {
        return "add-New-Student";
    }

    @PostMapping("saveStudentData")
    public String saveStudentData(@RequestParam String studentName, @RequestParam String studentPhone, @RequestParam String studentAddress, @RequestParam int sectionNumber, HttpSession httpSession) {
        nodeService.addNewStudent(studentName, studentPhone, studentAddress, sectionNumber, httpSession);
        return "success";
    }

    //show all students data
    @GetMapping("showAllStudentData")
    public String showAllStudentData(HttpSession httpSession, Model model) {
        nodeService.showAllStudentData(httpSession,model);
        return "show-All-Student-Data";
    }

    //filtered students
    @GetMapping("findStudent")
    public String findStudentPage() {
        return "find-Student-page";
    }
    //BY ID
    @GetMapping("filteredByID")
    public String findStudentByIdPage() {
        return "find-student-by-id";
    }
    @PostMapping("showStudent")
    public String showStudentByID(@RequestParam("ID")String ID,HttpSession httpSession, Model model){
        nodeService.getById(ID,httpSession,model);
        return "show-All-Student-Data";
    }
    //BY NAME
    @GetMapping("filteredByName")
    public String findStudentByNamePage() {
        return "find-student-by-name";
    }
    @PostMapping("filteredStudent")
    public String showStudentByName(@RequestParam("name")String name,HttpSession httpSession, Model model){
        nodeService.filtered("name", name, httpSession,  model);
        return "show-All-Student-Data";

    }
    //by section number
    @GetMapping("filteredBySectionNumber")
    public String filteredBySectionNumberPage() {
        return "select-section";
    }

    @PostMapping("studentWithSameSection")
    public String showSectionStudent(@RequestParam("sectionNumber")String sectionNumber,HttpSession httpSession, Model model){
        nodeService.filtered( "sectionNumber",sectionNumber, httpSession,  model);
        return "show-All-Student-Data";

    }
    //by address
    @GetMapping("filteredByAddress")
    public String filteredByAddress() {
        return "filtered-By-Address";
    }

    @PostMapping("studentWithSameAddress")
    public String studentWithSameAddress(@RequestParam("address")String address,HttpSession httpSession, Model model){
        nodeService.filtered( "address",address, httpSession,  model);
        return "show-All-Student-Data";

    }

    //remove user
    @GetMapping("removePage")
    public String removeCustomer() {
        return "remove-student";
    }

    @PostMapping("remove")
    public String removeStudent(@RequestParam("ID") String ID, HttpSession httpSession) {
        if (nodeService.removeStudent(ID, httpSession)){
            return "success";
        }else
            return "failed";
    }

    //update address
    @GetMapping("updatePage")
    public String updateStudentData(HttpSession httpSession, Model model) {
        nodeService.showAllStudentData(httpSession,model);
        return "update-student-data";
    }
    @PostMapping("update/{propertyName}/{docId}")
    public String update(@PathVariable(value = "docId") String docId,@PathVariable(value = "propertyName") String propertyName, Model model,HttpSession httpSession) {
        httpSession.setAttribute("propertyName", propertyName);
        httpSession.setAttribute("docId", docId);

        return "put-new-value";
    }
    @PostMapping("afterUpdate")
    public String updateToNewValue(@RequestParam("newValue") String newValue, Model model,HttpSession httpSession) {
       nodeService.update(newValue,httpSession);
        nodeService.showAllStudentData(httpSession,model);
        return "update-student-data";
    }
    //readingSpecificProperties (get phone)
    @GetMapping("readingSpecificProperties")
    public String readingSpecificProperties() {
        return "specific-property";
    }
    @PostMapping("getPhone")
    public String showStudentBdyID(@RequestParam("ID")String ID,HttpSession httpSession, Model model){
        nodeService.readingSpecificProperties("phone",ID,httpSession,model);
        return "specific-property";
    }
    //add new instructor
    @GetMapping("addNewInstructor")
    public String addNewInstructor() {
        return "add-new-instructor";
    }

    @PostMapping("saveInstructorData")
    public String saveInstructorData(@RequestParam String instructorName, @RequestParam String subjectName, HttpSession httpSession, Model model) {
        nodeService.addNewInstructor(instructorName, subjectName, httpSession);
        return "success";
    }
    //show all instructor
    @GetMapping("allInstructors")
    public String allInstructors(HttpSession httpSession, Model model) {
        nodeService.showAllInstructors(httpSession,model);
        return "show-all-instructor";
    }
    @GetMapping("loadBalance")
    public String showLoadBalance(HttpSession httpSession, Model model) {
        nodeService.showAllAffinityData(httpSession,model);
        return "affinity-load-balance";
    }

}

