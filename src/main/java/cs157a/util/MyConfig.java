package cs157a.util;

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads database settings from myConfig.json on the classpath.
 * This file is git-ignored, so each team member keeps their own copy.
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