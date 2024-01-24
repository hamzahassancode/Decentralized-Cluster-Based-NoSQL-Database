package com.example.NodeVM.repo;

import com.example.NodeVM.indexing.Indexing;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static com.example.NodeVM.DATA.GetPath.DATABASE_PATH;
import static com.example.NodeVM.DATA.GetPath.DATABASE_SCHEMAS_PATH;
import static com.example.NodeVM.repo.RegisterRepo.convertFileToString;

public class DocumentRepo implements DocumentDAO{
    private Indexing indexing = Indexing.getInstance();


    private String getCollectionPath(String dbName, String collectionName) {
        return DATABASE_PATH + dbName + "/" + collectionName + ".json";
    }
    @Override
    public synchronized void addDoc(String DBName,String collectionName,String documentJson){
        String collectionPath=getCollectionPath(DBName,collectionName);
        JSONArray jsonArray = new JSONArray(convertFileToString(collectionPath));
        JSONObject jsonObject = new JSONObject(documentJson);
        jsonArray.put(jsonObject);
        RegisterRepo.writeJSONArrayToFile(jsonArray,collectionPath);
    }

    @Override
    public boolean deleteDoc(String DBName,String collectionName,String docID) {
        String collectionPath=getCollectionPath(DBName,collectionName);
        JSONArray jsonArray = new JSONArray(convertFileToString(collectionPath));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("_id").equals(docID)) {
                jsonArray.remove(i);
                break;
            }
        }
        RegisterRepo.writeJSONArrayToFile(jsonArray,collectionPath);
        return true;
    }
    @Override
    public synchronized void updateDoc(String DBName,String collectionName, String docID, JSONObject newObj) {
        String collectionPath=getCollectionPath(DBName,collectionName);
        JSONArray jsonArray = new JSONArray(convertFileToString(collectionPath));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("_id").equals(docID)) {
                jsonArray.put(i, newObj);
                break;
            }
        }

        RegisterRepo.writeJSONArrayToFile(jsonArray, collectionPath);

    }

    @Override
    public JSONArray filteredDoc(String DBName, String collectionName, String propertyName, String propertyValue) {
        //TRY FROM INDEXING
        JSONArray findInIndexing = indexing.getFromIndex(DBName, collectionName, propertyName, propertyValue);
        if (!findInIndexing.isEmpty()) {
            System.out.println("GET FROM INDEXING"+ findInIndexing);

            return findInIndexing;
        }

        //try from file
        String collectionPath=getCollectionPath(DBName,collectionName);
        JSONArray filejsonArray = new JSONArray(convertFileToString(collectionPath));
        JSONArray filteredData = new JSONArray();
        for (int i = 0; i < filejsonArray.length(); i++) {
            JSONObject jsonObject = filejsonArray.getJSONObject(i);
            if (jsonObject.has(propertyName) && jsonObject.get(propertyName).toString().equals(propertyValue)) {
                filteredData.put(jsonObject);
            }
        }
        if (filejsonArray==null)
            return new JSONArray();

        return filteredData;
    }
    public String readingSpecificProperty(JSONObject jsonObject, String PropertyName) {
            if (jsonObject.has(PropertyName)) {
                String propertyValue=jsonObject.get(PropertyName).toString();
                return propertyValue;
            }
        return "notFound";
    }
    @Override
    public String getAllDocuments(String DBName,String collectionName) {
        String collectionPath = getCollectionPath(DBName, collectionName);
        File collectionFile=new File(collectionPath);
        try {
            return FileUtils.readFileToString(collectionFile, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public JSONObject getDocument(String DBName, String collectionName, String id) {
       //TRY FROM INDEXING

        JSONArray jsonArray = indexing.getFromIndex(DBName, collectionName, "_id", id);
        if (!jsonArray.isEmpty()) {
            System.out.println("GET FROM INDEXING"+ jsonArray);
            return  jsonArray.getJSONObject(0);
        }

        //TRY FROM FILE
        JSONArray specificDoc = filteredDoc(DBName,collectionName, "_id", id);
        if (specificDoc.isEmpty()) {
            return new JSONObject();
        }


        return (JSONObject) specificDoc.get(0); //  only one element matching the id
    }


    public boolean isValidDoc(String jsonDoc, String DBName, String collectionName) {
        try {
            String docWithoutId = removeIdFromDoc(jsonDoc);
            String schema = convertFileToString(DATABASE_SCHEMAS_PATH+ DBName + "/" + collectionName + ".json");

            // Parse the schema and create a schemaValidator
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance();
            JsonSchema schemaValidator = factory.getSchema(schema);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(docWithoutId);

            Set<ValidationMessage> errors = schemaValidator.validate(jsonNode);

            // If there are no errors will return true
            return errors.isEmpty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public JSONObject updatePropertyValueBasedOnSchema(String DBName,String collectionName, String propertyName,Object newValue,JSONObject currentObject) {
        String schemaPath=DATABASE_SCHEMAS_PATH+ DBName + "/" + collectionName + ".json";
        JSONObject schema = new JSONObject(convertFileToString(schemaPath));
        String dataType = schema.getJSONObject("properties").getJSONObject(propertyName).getString("type");

        switch (dataType) {
            case "string" -> newValue = newValue.toString();
            case "integer" -> newValue = Integer.parseInt(newValue.toString());
            case "number" -> newValue = Double.parseDouble(newValue.toString());
            case "boolean" -> newValue = Boolean.parseBoolean(newValue.toString());
            default -> {
                throw new IllegalArgumentException("Unsupported data type: " + dataType);
            }
        }
        // new json object
        JSONObject newObject = new JSONObject(currentObject.toString());
        newObject.put(propertyName, newValue);//will overwrite the existing value
        return newObject;
    }

    public static String addIdToDocument(String Document) {
        try {
            // Parse the JSON string to a JsonNode object
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(Document);

            if (!node.has("_id")) {
                ObjectNode objectNode = (ObjectNode) node;
                objectNode.put("_id", UUID.randomUUID().toString());
            }
            // Convert the JsonNode object back to a JSON string
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String removeIdFromDoc(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the JSON string to a JsonNode object
            JsonNode node = mapper.readTree(json);

            // Check if the _id field already exists
            if (node.has("_id")) {
                // Remove the _id field
                ((ObjectNode) node).remove("_id");
            }

            // Convert the JsonNode object back to a JSON string
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}
