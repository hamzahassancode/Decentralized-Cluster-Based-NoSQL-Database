package com.example.NodeVM.controller;

import com.example.NodeVM.model.Response;
import com.example.NodeVM.service.AffinityService;
import com.example.NodeVM.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.NodeVM.model.Response.Status.*;

@RestController
@RequestMapping("/affinity")
public class AffinityController {
    private ValidatorService validatorService;
    private AffinityService affinityService=AffinityService.getInstance();
    @Autowired
    public AffinityController(ValidatorService validatorService){
        this.validatorService=validatorService;
    }
    @GetMapping("addNodeDocAffinity/{id}/{nodeName}")
    public Response associateDocumentWithAffinityNode(@PathVariable(value = "id")String id,
                                                      @PathVariable(value = "nodeName")String nodeName,
                                                      @RequestHeader(value = "username")String adminUsername,
                                                      @RequestHeader(value = "token")String adminToken) throws IOException
    {

        if (!validatorService.isAdmin(adminUsername,adminToken)){
            return new Response(INTERNAL_ERROR,"not valid admin!");
        }
        affinityService.associateDocumentWithAffinityNode(id,nodeName);
        return new Response(SUCCESS,"add Node Affinity To Document successfully");

    }
    @GetMapping("/getAllDocToNodeAffinity")
    public Response getAllDocToNodeAffinity() {
        return affinityService.getAllDocToNodeAffinity();
    }

    @GetMapping("/setAffinityNode")
    public Response setAffinityNode()  {
        affinityService.setAffinityNode();
        return new Response(SUCCESS, "Affinity set successfully!");

    }

    @GetMapping("/clearAffinityNode")
    public Response clearAffinityNode() throws IOException {
        affinityService.clearAffinityNode();
        return new Response(SUCCESS, "Affinity cleared successfully!");
    }

    @GetMapping("/isAffinity")
    public boolean isAffinity() throws IOException {
        return affinityService.isAffinityEnabled();
    }

    @GetMapping("/setCurrentNode/{nodeName}")
    public Response setCurrentNode(@PathVariable("nodeName") String nodeName)  {
        affinityService.setCurrentNode(nodeName);
        return new Response(SUCCESS, "set node successfully!");
    }

    @GetMapping("/getCurrentNode")
    public String getCurrentNode()  {
        return affinityService.getCurrentNode();
    }


}
