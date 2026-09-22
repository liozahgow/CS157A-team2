<%--
    The page shown after a successful login.

    It lives under WEB-INF, so a browser cannot open it directly - the only way
    in is through HomeServlet or TeamServlet, both of which sit behind
    AuthFilter.

    Two servlets render this same file:
      HomeServlet  sets no "teams" attribute  -> the hint text is shown
      TeamServlet  sets the "teams" list      -> the table is shown
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.Team, cs157a.model.User" %>

<%
    // The user object was put into the session by LoginServlet.
    User user = (User) session.getAttribute("user");

    // Put there by TeamServlet. Null when HomeServlet rendered this page.
    @SuppressWarnings("unchecked")
    List<Team> teams = (List<Team>) request.getAttribute("teams");
%>

<html>
<head>
  <title>Pitwall - Home</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Pitwall</h1>

  <p>
    Signed in as <%= user.getDisplayName() %>
    (<%= user.getEmail() %>) - role: <%= user.getRole() %>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/logout">Log out</a>
  </p>

  <%-- The button is a plain GET form, so the result has its own URL. --%>
  <form method="get" action="${pageContext.request.contextPath}/app/teams">
    <button type="submit">List all racing teams</button>
  </form>
</div>


<%-- ------------------------------ result area ------------------------------ --%>
<div class="panel">
  <h2>Result</h2>

<% if (teams == null) { %>

  <p class="hint">Press the button above to load the teams table.</p>

<% } else if (teams.isEmpty()) { %>

  <p class="hint">The teams table is empty. Did you run db/02_seed.sql?</p>

<% } else { %>

  <p class="hint"><%= teams.size() %> row(s) from the <code>teams</code> table.</p>

  <table>
    <tr>
      <th>team_id</th>
      <th>team_name</th>
      <th>series</th>
      <th>invite_code</th>
      <th>status</th>
      <th>created_at</th>
    </tr>

    <%-- One <tr> per Team object handed over by TeamServlet. --%>
    <% for (Team t : teams) { %>
      <tr>
        <td><%= t.getTeamId() %></td>
        <td><%= t.getTeamName() %></td>
        <td><%= t.getSeries() %></td>
        <td><%= t.getInviteCode() %></td>
        <td><%= t.getStatus() %></td>
        <td><%= t.getCreatedAt() %></td>
      </tr>
    <% } %>
  </table>

<% } %>
</div>

</body>
</html>
