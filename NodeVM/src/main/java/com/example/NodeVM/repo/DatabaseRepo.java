package com.example.NodeVM.repo;

import com.example.NodeVM.indexing.Indexing;
import com.example.NodeVM.model.Response;
import org.apache.commons.io.FileUtils;
import java.io.File;

import static com.example.NodeVM.DATA.GetPath.DATABASE_PATH;
import static com.example.NodeVM.DATA.GetPath.DATABASE_SCHEMAS_PATH;
import static com.example.NodeVM.model.Response.Status.*;

public class DatabaseRepo implements DatabaseDAO {

   private Indexing indexing = Indexing.getInstance();


    @Override
    public Response createDatabase(String DBName ) {
        File dbDirectory = new File(DATABASE_PATH + DBName);
            // Create the database directory
            if (!dbDirectory.mkdirs()) {
                return new Response(BAD_REQUEST, "error when create database");
            } else {
                // creating the schemas directory
                File schemasDirectory = new File(DATABASE_SCHEMAS_PATH+ DBName + "/");
                if (!schemasDirectory.mkdirs()) {
                    return new Response(BAD_REQUEST, "error when create database");
                }
            }
        return new Response(SUCCESS, "Database "+DBName+" created successfully.");

    }

    @Override
    public Response deleteDatabase( String DBName)  {
        File dbDirectory = new File(DATABASE_PATH + DBName);

        try {
            indexing.removeDBFromIndex(DBName);
            FileUtils.deleteDirectory(dbDirectory);
            return new Response(SUCCESS, "Database " + DBName + " deleted successfully");

        } catch (Exception e) {
            return new Response(INTERNAL_ERROR, "Error when deleting database");
        }
    }

    @Override
    public String showAllDatabases() {
    File dbDirectory = new File(DATABASE_PATH);

    if (dbDirectory.exists() && dbDirectory.isDirectory()) {
        File[] dirs = dbDirectory.listFiles();
        StringBuilder stringBuilder = new StringBuilder();

        if (dirs != null) {
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    stringBuilder.append(dir.getName()).append("  ");
                }
            }
        }else {
            return "";
        }
        return stringBuilder.toString();
    } else {
        return "Directory does not exist.";
    }}







}
