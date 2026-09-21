import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Scanner; // To read user input for pausing purposes

public class HW1_MySqlJdbcReadUpdateDelete {
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
		
		Scanner scanner = new Scanner(System.in);
		
		// Connection to MySql
		Connection connection = DriverManager.getConnection(url, username, password);
//		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HW1?serverTimezone=UTC", "root", "root");
		
		// Read row
		String selectSql = "SELECT ID_USER FROM USER WHERE USERNAME='Tzuhao Liu'";
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(selectSql);
//		rs.last(); //somehow this caused some issue
		rs.next();
		int id = rs.getInt("ID_USER");
		System.out.println("rs.getInt(\"ID_USER\") --> " + id);
		System.out.println("Original Table: ");
		
		// Print all records
		ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
		ResultSetMetaData metadata = resultSet.getMetaData();
		int columnCount = metadata.getColumnCount();

		int fieldWidth = 15;

		// Print column names
		for (int column = 1; column <= columnCount; column++) {
		    System.out.printf("%-" + fieldWidth + "s", metadata.getColumnLabel(column));
		}
		System.out.println();
		while (resultSet.next()) {
		    for (int column = 1; column <= columnCount; column++) {
		        System.out.printf("%-" + fieldWidth + "s", resultSet.getObject(column));
		    }

		    System.out.println();
		} //END while
		rs.close();
		statement.close();
		
		// Update row
		String updateSql = "UPDATE USER SET AGE=35 WHERE ID_USER=" + id;
		statement = connection.createStatement();
		statement.execute(updateSql);
		statement.close();
		
		System.out.println("\n\n" + updateSql);
		// Print all records
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM user");
		metadata = resultSet.getMetaData();
		columnCount = metadata.getColumnCount();

		fieldWidth = 15;

		// Print column names
		for (int column = 1; column <= columnCount; column++) {
		    System.out.printf("%-" + fieldWidth + "s", metadata.getColumnLabel(column));
		}
		System.out.println();
		while (resultSet.next()) {
		    for (int column = 1; column <= columnCount; column++) {
		        System.out.printf("%-" + fieldWidth + "s", resultSet.getObject(column));
		    }

		    System.out.println();
		} //END while
		
		System.out.print("\n\nEnter anything to continue deleting the record... ");
        // 3. Read a full line of text
        String name = scanner.nextLine();
		
		// Delete row
		String deleteSql = "DELETE FROM USER WHERE ID_USER=" + id;
		statement = connection.createStatement();
		statement.execute(deleteSql);
		statement.close();
		
		System.out.println("\n\n" + deleteSql);
		// Print all records
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM user");
		metadata = resultSet.getMetaData();
		columnCount = metadata.getColumnCount();

		fieldWidth = 15;

		// Print column names
		for (int column = 1; column <= columnCount; column++) {
		    System.out.printf("%-" + fieldWidth + "s", metadata.getColumnLabel(column));
		}
		System.out.println();
		while (resultSet.next()) {
		    for (int column = 1; column <= columnCount; column++) {
		        System.out.printf("%-" + fieldWidth + "s", resultSet.getObject(column));
		    }

		    System.out.println();
		} //END while
		
		// Close connection
		connection.close();
		System.out.println("\n\nCompleted!!");
	} //END main
} //END public class
