import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HW1_MsqlJdbcInsert {
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
		
		// Insert row
		java.util.Date now = new java.util.Date();
		java.sql.Date sqlDate = new java.sql.Date(now.getTime());
		String insertSql = "INSERT INTO USER (USERNAME, AGE, CREATED_DATE) "
				          + "VALUES ('Tzuhao Liu', 18, '" + sqlDate + "')";
		Statement statement = connection.createStatement();
		statement.execute(insertSql);
		
		// Query 
		
		String querySql = "SELECT * FROM USER";
		Statement st=connection.createStatement();
		ResultSet rs=st.executeQuery(querySql);
		while(rs.next())
		{
		    // rs.getString(1); //or rs.getString("column name");
			String Fullname = rs.getString("USERNAME");
            int age = rs.getInt("AGE");
            String Date = rs.getString("CREATED_DATE");

            System.out.println(Fullname + "    " + age + "    " + Date);
		}
		
		// Close connection
		statement.close();
		rs.close();
		connection.close();
		System.out.println("Completed!!");
	} //END main
} //END public class
