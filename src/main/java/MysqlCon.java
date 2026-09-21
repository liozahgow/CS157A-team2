import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MysqlCon {

	public static void main(String[] args) throws Exception {
		/* Step 00: My Configurations */
		String hostname      = "localhost"; // Valid Values: "localhost" OR "127.0.0.1"
		String sql_port      = "3306";  // Default port is 3306
		String database_name = "xxx_your_last_name_xxx";
		String username      = "root";
		String password      = "xxxxxxxxxx";
		String url = "jdbc:mysql://" + hostname + ":" + sql_port + "/" + database_name +
				"?useSSL=false" +
				"&allowPublicKeyRetrieval=true" +
				"&serverTimezone=UTC";
		String table_name    = "student";
		

		/* Step 01: Create Connection to MySql */
		Class.forName("com.mysql.cj.jdbc.Driver"); // "com.mysql.jdbc.Driver" is deprecated
		Connection connection = DriverManager.getConnection(url, username, password);
		System.out.println("Database: " + database_name + " successfully opened. \n");
		
		/* Step 02: Create query "Statement" instance */
		Statement statement = connection.createStatement();
		
		/* Step 03: Construct SQL-Query string/statement */
		String querySql = "SELECT * FROM " + table_name;
		
		/* Step 04: Execute SQL query and get result (ResultSet) */  
		ResultSet rs = statement.executeQuery(querySql);
		
		/* Step 05: Print Results */
		String format = "%-15s%-15s%-15s%n";
		System.out.printf(format, "SJSU ID", "Name", "Major");
		
		while(rs.next())
		{
		    rs.getString(1); //or rs.getString("column name");
		    int student_id = rs.getInt(1);
			String student_name = rs.getString(2);
            String student_major = rs.getString(3);

            System.out.printf(
                    "%-15d%-15s%-15s%n",
                    student_id,
                    student_name,
                    student_major);
		} //END while
		
		// Step 06: Close connection
		rs.close();
		statement.close();
		connection.close();
	} //END main
} //END public class MysqlCon
