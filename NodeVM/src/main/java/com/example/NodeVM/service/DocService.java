package com.example.NodeVM.service;

import com.example.NodeVM.broadcast.Broadcast;
import com.example.NodeVM.model.Response;
import com.example.NodeVM.repo.DocumentRepo;
import com.example.NodeVM.indexing.Indexing;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.example.NodeVM.model.Response.Status.*;
import static com.example.NodeVM.model.Response.Status.NOT_FOUND;
import static com.example.NodeVM.repo.DocumentRepo.addIdToDocument;

public class DocService {
   private Indexing indexing = Indexing.getInstance();

    private ValidatorService validatorService = new ValidatorService();
    private DocumentRepo documentRepo = new DocumentRepo();
    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(DocService.class);
    private Broadcast broadcastToAll = Broadcast.getInstance();
    private AffinityService affinityService = AffinityService.getInstance();
    public Response addDoc( String DBName, String collectionName, String document, String username, String token, boolean broadcast) throws IOException {


        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();
        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't create document, NOT valid user!");
        }
        if (broadcast) {
            documentRepo.addDoc(DBName, collectionName, document);
           indexing.addToIndex(DBName,collectionName,new JSONObject(document));
            return new Response(SUCCESS, "add Document successfully");
        }
        if (!validatorService.isDatabaseExists(DBName)) {
            return new Response(INTERNAL_ERROR, "Database does not exist.");
        }
        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }
        if (!documentRepo.isValidDoc(document, DBName,collectionName)) {
            return new Response(INTERNAL_ERROR, "Invalid JSON object");
        }

        if (affinityService.isAffinityEnabled()) {

            document = addIdToDocument(document);

            String endPoint = "document/addDoc/" + DBName + "/" + collectionName;
           broadcastToAll.broadcast(endPoint,"POST",  document);

            //ensure the Nodes will register this document in own database (DOC_affinity.json)
            letNodesRegisterTheAffinityOfThisDoc(document);

            // Passing the affinity Node to the next Node in cycle
            affinityService.cycleAffinityNode();

        } else {
            broadcastToAll.sendDocToNodeHasAffinity(DBName, collectionName, document);

        }
        return new Response(SUCCESS, "Document added successfully");

    }
public void letNodesRegisterTheAffinityOfThisDoc(String document ){
    JSONObject jsonObject = new JSONObject(document);
    String id = jsonObject.getString("_id");
    String urlToAddAffinity = "affinity/addNodeDocAffinity/" + id + "/" + affinityService.getCurrentNode();
    broadcastToAll.broadcast(urlToAddAffinity, "GET", "");
}

    public Response deleteDoc( String DBName, String collectionName, String docID, String username, String token, boolean broadcast) throws IOException {

        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't delete document, NOT valid user!");
        }
        if (!validatorService.isDatabaseExists(DBName)) {
            return new Response(INTERNAL_ERROR, "Database does not exist.");
        }
        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }

        if (broadcast) {

            if (documentRepo.deleteDoc(DBName, collectionName, docID)) {
                indexing.removeDocFromIndex(DBName,collectionName,docID);
                affinityService.removeNodeAffinity(docID);

                return new Response(SUCCESS, "Document deleted successfully");
            }else {
                return new Response(NOT_FOUND, "Can't find document "+docID+"in collection "+collectionName);
            }

        }

        String affinityNode = affinityService.getNodeAffinityToSpecificDoc(docID);
        if (affinityNode.equals("can't get node affinity to this document"))
        {
            return new Response(NOT_FOUND, "Can't find document");
        }


        if (affinityNode.equals(affinityService.getCurrentNode())) {

            String endPoint =  "document/deleteDoc/" + DBName + "/" + collectionName + "/" + docID;
            broadcastToAll.broadcast(endPoint,"DELETE", "");
        }else {
            //send to affinity node
            String url = "http://" + affinityNode + ":4001/document/deleteDoc/" + DBName + "/" + collectionName + "/" + docID;
            HttpHeaders headers = new HttpHeaders();
            headers.set("username", "root");
            headers.set("token", "root123");
            //broadcast false by default
            HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
            restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        }
        return new Response(SUCCESS, "Document deleted successfully");

    }

    public Response updateDoc( String DBName, String collectionName, String docID,String propertyName, Object newValue, String username, String token, boolean broadcast, String redirectedValue) throws IOException{

        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't create document, NOT valid user!");
        }

        if (!validatorService.isDatabaseExists(DBName)) {
            return new Response(INTERNAL_ERROR, "Database does not exist.");
        }

        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }

        JSONObject currentObject = documentRepo.getDocument(DBName, collectionName, docID);

        if (currentObject.isEmpty()) {
            return new Response(NOT_FOUND, "Document with ID " + docID + " is not found in " + collectionName);
        }

        JSONObject newObjectValue=documentRepo.updatePropertyValueBasedOnSchema(DBName,collectionName,propertyName,newValue,currentObject);

        if (!documentRepo.isValidDoc(newObjectValue.toString(), DBName,collectionName)) {
            return new Response(INTERNAL_ERROR, "Invalid JSON object");
        }

        if (broadcast){
            documentRepo.updateDoc(DBName,collectionName,docID,newObjectValue);
            indexing.updateDocumentIndexing(DBName,collectionName,newObjectValue);

         return new Response(SUCCESS,"Document updated successfully");
        }

        String affinityNodeToThisDoc=affinityService.getNodeAffinityToSpecificDoc(docID);
        if (affinityNodeToThisDoc.equals("can't get node affinity to this document"))
        {
            return new Response(NOT_FOUND, "Can't find document "+docID+"in collection "+collectionName);
        }
        logger.info("the affinity Node To This Doc = " +affinityNodeToThisDoc);
        logger.info("is this node the affinity node ? " +affinityNodeToThisDoc.equals(affinityService.getCurrentNode()));

        if (affinityNodeToThisDoc.equals(affinityService.getCurrentNode())){
            String currentObjectValue = currentObject.get(propertyName).toString();
            logger.info("redirected Value = " + redirectedValue);
            logger.info("current value =  " + currentObjectValue);
            logger.info("update value to  =" + newValue);

            //Optimistic locking
            //if the redirectedValue is equals("same value inside affinity") mean that the request come from the affinity node itself so no redirectedValue provide in header
            if (!redirectedValue.equals("same value inside affinity")) {
                logger.info("Data that was redirected from the node dose not hava affinity to doc =" + redirectedValue);
                logger.info("and my knowledge the value =" + currentObjectValue);
                logger.info("update value to  =" + newValue);

                if (!redirectedValue.equals(currentObjectValue)) {
                    logger.info("the value inside node dose not hava affinity =" + redirectedValue);
                    logger.info("and my knowledge the value =" + currentObjectValue);
                    logger.warn("can not update the value to  =" + newValue);

                    return new Response(NOT_FOUND, "The request failed. Your old data does not resemble the existing data. The update process failed  ");
                }
            }

            logger.warn("broadcast done");

            String endPoint = "document/updateDoc/" + DBName + "/" + collectionName + "/" + docID + "/" + propertyName + "/" + newValue;
            broadcastToAll.broadcast(endPoint,"POST","");

        }else {
            String url = "http://" + affinityNodeToThisDoc + ":4001/document/updateDoc/" + DBName + "/" + collectionName + "/" + docID + "/" + propertyName + "/" + newValue;
            // Sending the current version of data to the affinity.
            HttpHeaders headers = new HttpHeaders();
            headers.set("username", "root");
            headers.set("token", "root123");
            headers.set("redirectedValue", currentObject.get(propertyName).toString());

            HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
            restTemplate.postForObject(url, requestEntity, String.class);
        }

        return new Response(SUCCESS, "Document updated successfully.");
    }
    public Response getDoc(String DBName, String collectionName, String docID, String username, String token) throws IOException {

        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();
        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't get document, NOT valid user!");
        }
        if (!validatorService.isDatabaseExists(DBName)) {
            return new Response(INTERNAL_ERROR, "Database does not exist.");
        }
        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }
        JSONObject obj = documentRepo.getDocument(DBName, collectionName, docID);
        if (obj.isEmpty()){
            return new Response(NOT_FOUND, "Could Not Find Any document");
        }
        return new Response(SUCCESS, obj.toString());
    }


    public Response getAllDocuments(String DBName, String collectionName, String username, String token
    ) throws IOException {
        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();
        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "not valid user");
        }
        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }else {
            try {
                String data = documentRepo.getAllDocuments(DBName, collectionName);
                return new  Response(SUCCESS,data);
            } catch (Exception e) {
                return new  Response(SUCCESS,"error when trying to get all document "+e);

            }
        }
    }
    public Response  readingSpecificProperties(String DBName, String collectionName, String docID, String propertyName, String username, String token)  {

        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, "Can't get document, NOT valid user!");
        }

        if (!validatorService.isDatabaseExists(DBName)) {
            return new Response(INTERNAL_ERROR, "Database does not exist.");
        }

        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }

        JSONObject obj = documentRepo.getDocument(DBName, collectionName, docID);
        if (obj.isEmpty() ){
            return new Response(NOT_FOUND, "Could Not Find Any document");
        }

        String propertyValue=documentRepo.readingSpecificProperty(obj,propertyName);
        if (propertyValue.equals("notFound")) {
            return new Response(NOT_FOUND, "There is no property with this name in Document " + docID);
        }else {
            return new Response(SUCCESS,  propertyName+" : "+propertyValue );
        }


    }
    public Response filterDoc( String DBName, String collectionName, String propertyName, String propertyValue, String username, String token) throws IOException {

        DBName = DBName.toLowerCase();
        collectionName = collectionName.toLowerCase();

        if (!validatorService.isValidUser(username, token)) {
            return new Response(INTERNAL_ERROR, " NOT valid user!");
        }
        if (!validatorService.isDatabaseExists(DBName)) {
            return new Response(INTERNAL_ERROR, "Database does not exist.");
        }
        if (!validatorService.isCollectionExists(DBName, collectionName)) {
            return new Response(INTERNAL_ERROR, "collection does not exist.");
        }

        JSONArray filterDoc=documentRepo.filteredDoc(DBName,collectionName,propertyName,propertyValue);
        if (filterDoc.isEmpty()) {
            return new Response(NOT_FOUND,"Could Not Find Any document");
        }
        return new Response(SUCCESS,filterDoc.toString());

    }
}
