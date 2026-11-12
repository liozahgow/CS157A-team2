<%--
    FR10 - team approval. Rendered only by AdminServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.Team, cs157a.util.Html" %>

<%
    @SuppressWarnings("unchecked")
    List<Team> teams = (List<Team>) request.getAttribute("teams");

    String error = (String) request.getAttribute("error");
    String done  = request.getParameter("done");
    String ctx   = request.getContextPath();

    int pending = 0;
    for (Team t : teams) { if ("Pending".equals(t.getStatus())) { pending++; } }
%>

<html>
<head>
  <title>Pitwall - Team approval</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Team approval</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <a href="<%= ctx %>/app/admin">Team approval</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>

  <p class="hint">
    A team is created as Pending when a manager signs up. Approving it lets
    the team be used; suspending it stops the team again. The administrator
    belongs to no team and never looks at a team engineering data.
  </p>
</div>

<div class="panel">
  <h2><%= teams.size() %> team(s), <%= pending %> waiting for approval</h2>

  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } else if (done != null) { %>
    <p class="hint">Status changed to <%= Html.escape(done) %>.</p>
  <% } %>

  <table>
    <tr>
      <th>Team</th>
      <th>Series</th>
      <th>Invite code</th>
      <th>Members</th>
      <th>Status</th>
      <th>Created</th>
      <th></th>
    </tr>

    <% for (Team t : teams) { %>
      <tr>
        <td><%= Html.escape(t.getTeamName()) %></td>
        <td><%= Html.escape(t.getSeries()) %></td>
        <td><%= Html.escape(t.getInviteCode()) %></td>
        <td><%= t.getMemberCount() %></td>
        <td><%= Html.escape(t.getStatus()) %></td>
        <td><%= t.getCreatedAt() %></td>
        <td>
          <% if (!"Approved".equals(t.getStatus())) { %>
            <form method="post" action="<%= ctx %>/app/admin">
              <input type="hidden" name="teamId" value="<%= t.getTeamId() %>">
              <input type="hidden" name="action" value="approve">
              <button type="submit">Approve</button>
            </form>
          <% } %>
          <% if (!"Suspended".equals(t.getStatus())) { %>
            <form method="post" action="<%= ctx %>/app/admin">
              <input type="hidden" name="teamId" value="<%= t.getTeamId() %>">
              <input type="hidden" name="action" value="suspend">
              <button type="submit">Suspend</button>
            </form>
          <% } %>
        </td>
      </tr>
    <% } %>
  </table>
</div>

</body>
</html>
