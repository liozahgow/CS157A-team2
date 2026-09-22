package cs157a.util;

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads database settings from myConfig.json on the classpath.
 * This file is git-ignored, so each team member keeps their own copy.
 * 
 * 
 * Example of using MyConfig:

import cs157a.util.MyConfig;  // Step 01: import MyConfig

public class MyTest {
	public static void main(String [] args) {
		// Step 02: Use the singleton method get(key) to get the value
		String database_name = MyConfig.get("database_name");
		String database_user = MyConfig.get("database_user");
		String database_pswd = MyConfig.get("database_pswd");
		
		System.out.println("database_name: " + database_name);
		System.out.println("database_user: " + database_user);
		System.out.println("database_pswd: " + database_pswd);
				
	} //END main()
} //END public class MyTest

 */
public class MyConfig {

    private static final String CONFIG_FILE = "/myConfig.json";
    private static JsonNode root;

    /** Reads and parses the JSON file once, then caches the result. */
    private static JsonNode root() {
        if (root == null) {
            try (InputStream in = MyConfig.class.getResourceAsStream(CONFIG_FILE)) {
                if (in == null) {
                    throw new IllegalStateException(
                            "Cannot find " + CONFIG_FILE + " on the classpath. "
                            + "Copy myConfig.example.json to myConfig.json and fill in your own values.");
                }
                root = new ObjectMapper().readTree(in);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to read " + CONFIG_FILE, e);
            }
        }
        return root;
    }

    /** Returns the value for one key, for example get("hostname"). */
    public static String get(String key) {
        JsonNode node = root().get(key);
        if (node == null) {
            throw new IllegalStateException("Missing key \"" + key + "\" in " + CONFIG_FILE);
        }
        return node.asText();
    }

    /** Builds the JDBC url from the configured host, port and database name. */
    public static String getJdbcUrl() {
        return "jdbc:mysql://" + get("hostname") + ":" + get("sql_port") + "/" + get("database_name")
                + "?useSSL=false"
                + "&autoReconnect=true"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=UTC";
    }
}