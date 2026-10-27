<%--
    FR3 - the team roster. Rendered only by RosterServlet.
    Keep scriptlets in plain old Java: Tomcat compiles JSPs at an older Java level.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.RosterMember, cs157a.util.Html" %>

<%
    boolean showInactive = (Boolean) request.getAttribute("showInactive");

    @SuppressWarnings("unchecked")
    List<RosterMember> members = (List<RosterMember>) request.getAttribute("members");

    String ctx = request.getContextPath();
%>

<html>
<head>
  <title>Pitwall - Team roster</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Team roster</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <a href="<%= ctx %>/app/roster">Roster</a>
    <a href="<%= ctx %>/app/cars">Cars</a>
    <a href="<%= ctx %>/app/tires">Tire sets</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>
</div>

<div class="panel">
  <h2><%= members.size() %> member(s)</h2>

  <p>
    <% if (showInactive) { %>
      <a href="<%= ctx %>/app/roster">Hide members who left the team</a>
    <% } else { %>
      <a href="<%= ctx %>/app/roster?inactive=1">Show members who left the team</a>
    <% } %>
  </p>

  <table>
    <tr>
      <th>Name</th>
      <th>Email</th>
      <th>Role</th>
      <th>Car #</th>
      <th>Specialty</th>
      <th>Office phone</th>
      <th>Status</th>
    </tr>

    <% for (RosterMember m : members) { %>
      <tr>
        <td><%= Html.escape(m.getDisplayName()) %></td>
        <td><%= Html.escape(m.getEmail()) %></td>
        <td><%= Html.escape(m.getRole()) %></td>
        <td><%= m.getDriverNumber() == null ? "" : m.getDriverNumber().toString() %></td>
        <td><%= Html.escape(m.getSpecialty()) %></td>
        <td><%= Html.escape(m.getOfficePhone()) %></td>
        <td><%= m.isActive() ? "Active" : "Left team" %></td>
      </tr>
    <% } %>
  </table>
</div>

</body>
</html>