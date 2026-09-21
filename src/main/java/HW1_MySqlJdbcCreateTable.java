import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class HW1_MySqlJdbcCreateTable {
	public static void main(String[] args) throws Exception {
		/* My Configurations */
		String hostname = "localhost"; // Valid Values: "localhost" OR "127.0.0.1"
		String sql_port = "3306";  // Default port is 3306
		String database = "hw1";
		String username = "root";
		String password = "xxxxxxxxxx";

		String url = "jdbc:mysql://" + hostname + ":" + sql_port + "/" + database + 
				"?" + 
				"useSSL=false" + 
				"&autoReconnect=true" +
				"&allowPublicKeyRetrieval=true" + 
				"&serverTimezone=UTC";
		
		Class.forName("com.mysql.cj.jdbc.Driver"); // "com.mysql.jdbc.Driver" is deprecated
		
		// Connection to MySql
		Connection connection = DriverManager.getConnection(url, username, password);
		
		// Create table
		String createSql = "CREATE TABLE USER (ID_USER INT NOT NULL AUTO_INCREMENT, " 
				+ "USERNAME VARCHAR(45) NULL, AGE INT NULL, "
				+ "CREATED_DATE DATE NOT NULL, PRIMARY KEY (ID_USER))";
		Statement statement = connection.createStatement();
		statement.execute(createSql);
		statement.close();
		
		// Close connection
		connection.close();
		System.out.println("Completed!!");
	} //END main
} //END public class

