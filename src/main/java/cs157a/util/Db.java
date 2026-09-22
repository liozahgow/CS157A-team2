package cs157a.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Hands out database connections.
 *
 * This class only opens the door - it contains no business SQL. Every SELECT,
 * INSERT and UPDATE belongs in the cs157a.dao package.
 */
public class Db {

    /**
     * Registers the MySQL driver once, the first time this class is used.
     *
     * Since JDBC 4.0 a driver normally registers itself automatically, but that
     * does not happen inside Tomcat: Tomcat initialises java.sql.DriverManager
     * during startup, before any web application is loaded, so the automatic
     * scan never sees a driver sitting in our WEB-INF/lib. Asking for the class
     * by name loads it and triggers its own registration.
     *
     * This is why the old my_3_tier_demo.jsp also had a Class.forName call.
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MySQL JDBC driver not found. Check the mysql-connector-j "
                    + "dependency in pom.xml and that it reaches WEB-INF/lib.", e);
        }
    }

    /**
     * Opens a new connection using the values in myConfig.json.
     * The caller must close it, normally with try-with-resources.
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(
                MyConfig.getJdbcUrl(),
                MyConfig.get("database_user"),
                MyConfig.get("database_pswd"));
    }
}
