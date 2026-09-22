<%@ page import="java.sql.*"%>

<html>
<head>
  <title>Pitwall</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<h1>Pitwall SQL Database</h1>

<table border="1">
  <tr>
    <td>SJSU ID</td>
    <td>Name</td>
    <td>Major</td>
  </tr>
    <%
        /* My Configurations */
        
        String hostname = "localhost"; // Valid Values: "localhost" OR "127.0.0.1"
        String sql_port = "3306";  // Default port is 3306
        String database = "xxx_your_db_name_xxx";
        String username = "root";
        String password = "xxxxxxxxxx";
        String table_name = "student";
        //String table_name = "user";  // The column name will be off because it's hard-coded in html above

        String url = "jdbc:mysql://" + hostname + ":" + sql_port + "/" + database + 
            "?" + 
            "useSSL=false" + 
            "&autoReconnect=true" +
            "&allowPublicKeyRetrieval=true" + 
            "&serverTimezone=UTC";
        
        try {
            java.sql.Connection con;
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);

            out.println(database + " database successfully opened.<br/><br/>");
            out.println("Initial entries in table \"" + table_name + "\": <br/>");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table_name);
            while (rs.next()) {
              out.println("<tr>" + "<td>" +  rs.getInt(1) + "</td>"+ "<td>" +    rs.getString(2) + "</td>"+   "<td>" + rs.getString(3) + "</td>"  + "</tr>");
            } //END while
            rs.close();
            stmt.close();
            con.close();
        } catch(SQLException e) {
            out.println("SQLException caught: " + e.getMessage());
        } //END try-catch
    %>
</table>
</body>
</html>