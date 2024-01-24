package com.example.NodeVM.indexing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.NodeVM.DATA.GetPath.DATABASE_PATH;
import static com.example.NodeVM.repo.RegisterRepo.convertFileToString;


public class Indexing {

    // private static Map<Map<String,Map<String,Map<String,String>>>, List<JSONObject>> indexingMap = new ConcurrentHashMap<>();
    private static final int MAX_INDEX_SIZE = 1000;

    private static Map<IndexingModel, JSONArray> indexingMap = new ConcurrentHashMap<>();
    private static Indexing instance ;

    private Indexing() {
    }
    public static Indexing getInstance() {
        if (instance==null) {
            return instance=new Indexing();
        }
        else {
            return instance;
        }

    }

    private synchronized void removeOldestEntries() {
        Map<IndexingModel, JSONArray> lruMap = new LinkedHashMap<>(MAX_INDEX_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<IndexingModel, JSONArray> eldest) {
                // Remove the eldest entry when the map size exceeds the maximum
                return size() > MAX_INDEX_SIZE;
            }
        };
        System.out.println("RESIZE THE INDEXING NOW");
        // Copy the existing indexing map into the LinkedHashMap to maintain access order
        lruMap.putAll(indexingMap);

        // Update the indexing map with the LinkedHashMap (it will contain the most recently accessed entries)
        indexingMap = lruMap;
    }
    public  void addToIndex(String dbName, String collectionName, JSONObject document) {
        if (document == null) {
            return;
        }

        // Check if the map size exceeds the maximum size
        if (indexingMap.size() >= MAX_INDEX_SIZE) {
            removeOldestEntries();
        }

        document.keySet().forEach(propertyName -> {

            String propertyValue = document.get(propertyName).toString();

            IndexingModel indexingModel = new IndexingModel(dbName, collectionName, propertyName, propertyValue);

            JSONArray documentArray = indexingMap.computeIfAbsent(indexingModel, k -> new JSONArray());
            documentArray.put(document);
            indexingMap.put(indexingModel,documentArray);
        });

    }//The time complexity is O(K), where K is the number of keys in the document



    public JSONArray getFromIndex(String dbName, String collectionName, String propertyName, Object propertyValue) {

        IndexingModel indexingModel = new IndexingModel(dbName, collectionName, propertyName, propertyValue.toString());

        JSONArray getJsonArray=indexingMap.get(indexingModel);

        if (getJsonArray==null){
            return new JSONArray();
        }else {
            return getJsonArray;
        }
    }//The time complexity is O(1)

    public synchronized void updateDocumentIndexing(String dbName, String collectionName, JSONObject updatedDoc) {
        if (updatedDoc == null) {
            return;
        }
        removeDocFromIndex(dbName, collectionName, updatedDoc.get("_id").toString());
        addToIndex(dbName, collectionName, updatedDoc);

    }
    public synchronized void removeDocFromIndex(String dbName, String collectionName, String docId) {
        JSONObject document = getFromIndex(dbName, collectionName, "_id", docId).getJSONObject(0);

        for (String propertyName : document.keySet()) {//K

            Object value = document.get(propertyName);
            String propertyValue = String.valueOf(value);


            IndexingModel indexingModel = new IndexingModel(dbName, collectionName, propertyName, propertyValue);

            JSONArray documentArray = indexingMap.get(indexingModel);
            if (documentArray != null) {
                for (int i = 0; i < documentArray.length(); i++) {// N
                    JSONObject currentDoc = documentArray.getJSONObject(i);
                    if (currentDoc.equals(document)) {
                        documentArray.remove(i);
                        break;
                    }
                }
            }
        }
    }//The time complexity is O(K * N) in the worst case


    public synchronized void removeDBFromIndex(String dbName) {
        //Each entry in the set contains both the key (IndexingModel) and the value (JSONArray)
        indexingMap.entrySet().removeIf(entry -> entry.getKey().getDBName().equals(dbName));

    }//The time complexity is O(N), where N is the number of entries in the map.




    public synchronized void removeCollectionFromIndex(String dbName, String collectionName) {
        //Each entry in the set contains both the key (IndexingModel) and the value ( JSONArray)
        indexingMap.entrySet().removeIf(entry ->
                entry.getKey().getDBName().equals(dbName) &&
                entry.getKey().getCollectionName().equals(collectionName));
    }//The time complexity is O(N), where N is the number of entries in the map.


    public static void setupIndexing() {
        File allDBsDirectory = new File(DATABASE_PATH);
        for (String dbName : allDBsDirectory.list()) {
            File dbDir = new File(DATABASE_PATH + dbName);
            for (String collectionName : dbDir.list()) {
                JSONArray collectionArray = new JSONArray(convertFileToString(DATABASE_PATH + dbName + "/" + collectionName));
                for (int i = 0; i < collectionArray.length(); i++) {
                    JSONObject currentDoc = collectionArray.getJSONObject(i);
                    String collectionWithOutExtension = collectionName.substring(0, collectionName.toLowerCase().length() - 5);
                    instance.addToIndex(dbName, collectionWithOutExtension, currentDoc);
                }
            }
        }
    }//O(M * N * K),




    private class IndexingModel {
        private String DBName;
        private String collectionName;
        private String propertyName;
        private String propertyValue;
        public IndexingModel(String DBName, String collectionName, String propertyName, String propertyValue) {
            this.DBName = DBName;
            this.collectionName = collectionName;
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
        }
        public String getDBName() {
            return DBName;
        }

        public String getCollectionName() {
            return collectionName;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object)
                return true;
            if (object == null || getClass() != object.getClass())
                return false;
            IndexingModel other = (IndexingModel) object;
            return Objects.equals(this.DBName, other.DBName) &&
                    Objects.equals(this.collectionName, other.collectionName) &&
                    Objects.equals(this.propertyName, other.propertyName) &&
                    Objects.equals(this.propertyValue, other.propertyValue);
        }


        @Override
        public int hashCode() {
            return Objects.hash(DBName, collectionName, propertyName, propertyValue);
        }

    }
//public synchronized void updateDocumentIndexing(String dbName, String collectionName, JSONObject updatedDoc) {
//    if (updatedDoc == null) {
//        return;
//    }
//
//    String docId = updatedDoc.getString("_id");
//    if (docId == null) {
//        return;
//    }
//
//    IndexingModel indexingModel = new IndexingModel(dbName, collectionName, "_id", docId);
//
//    JSONArray documentArray = indexingMap.get(indexingModel);
//    if (documentArray != null) {
//        // Find the document to update in the array
//        for (int i = 0; i < documentArray.length(); i++) {//k
//            JSONObject currentDoc = documentArray.getJSONObject(i);
//            if (currentDoc.getString("_id").equals(docId)) {
//                // Remove the old document from the array
//                documentArray.remove(i);
//                // Add the updated document to the array
//                documentArray.put(updatedDoc);
//                // Remove the old entry from the indexing map
//                indexingMap.remove(indexingModel);
//                // Put the updated entry in the indexing map
//                indexingMap.put(indexingModel, documentArray);
//                break;
//            }
//        }
//    }
//}//o(k)
}