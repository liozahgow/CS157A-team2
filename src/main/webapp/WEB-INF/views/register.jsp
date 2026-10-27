<%--
    FR1 - the sign-up form.

    Rendered by RegisterServlet, both for a first visit (GET) and when a
    submitted form had a problem (POST). In the second case the servlet sets
    an "error" attribute, and every field is refilled from the request so the
    user does not have to type it all again.

    Everything the user typed goes through Html.escape before it is printed.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="cs157a.util.Html" %>

<%
    String error = (String) request.getAttribute("error");

    // Empty on a first visit; whatever was submitted after a failed POST.
    String role = request.getParameter("role");
    if (role == null) {
        role = "Driver";
    }
%>

<html>
<head>
  <title>Pitwall - Create an account</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Create an account</h1>

  <% if (error != null) { %>
      <p class="error"><%= Html.escape(error) %></p>
  <% } %>

  <form method="post" action="${pageContext.request.contextPath}/register">
    <p>
      <label for="displayName">Display name</label><br>
      <input type="text" id="displayName" name="displayName" size="34" maxlength="100" required
             value="<%= Html.escape(request.getParameter("displayName")) %>">
    </p>
    <p>
      <label for="email">Email</label><br>
      <input type="email" id="email" name="email" size="34" maxlength="255" required
             value="<%= Html.escape(request.getParameter("email")) %>">
    </p>
    <p>
      <label for="password">Password (at least 8 characters)</label><br>
      <%-- Never refilled: the password is not sent back to the browser. --%>
      <input type="password" id="password" name="password" size="34" minlength="8" required>
    </p>
    <p>
      <label for="phone">Phone (optional)</label><br>
      <input type="text" id="phone" name="phone" size="34" maxlength="20"
             value="<%= Html.escape(request.getParameter("phone")) %>">
    </p>

    <p>
      <label for="role">I am a</label><br>
      <select id="role" name="role">
        <option value="Driver"   <%= "Driver".equals(role)   ? "selected" : "" %>>Driver</option>
        <option value="Engineer" <%= "Engineer".equals(role) ? "selected" : "" %>>Race Engineer</option>
        <option value="Manager"  <%= "Manager".equals(role)  ? "selected" : "" %>>Team Manager</option>
      </select>
    </p>

    <%-- Drivers and engineers join an existing team. --%>
    <p id="joinTeam">
      <label for="inviteCode">Team invite code (drivers and engineers)</label><br>
      <input type="text" id="inviteCode" name="inviteCode" size="34" maxlength="8"
             value="<%= Html.escape(request.getParameter("inviteCode")) %>">
    </p>

    <p id="carNumber">
      <label for="driverNumber">Car number (drivers only, 0-999)</label><br>
      <input type="number" id="driverNumber" name="driverNumber" min="0" max="999"
             value="<%= Html.escape(request.getParameter("driverNumber")) %>">
    </p>

    <%-- Managers create a new team, which an administrator must approve. --%>
    <p id="newTeam">
      <label for="teamName">New team name (managers)</label><br>
      <input type="text" id="teamName" name="teamName" size="34" maxlength="100"
             value="<%= Html.escape(request.getParameter("teamName")) %>">
    </p>

    <p>
      <button type="submit">Create account</button>
    </p>
  </form>

  <p class="hint">
    Already have an account? <a href="${pageContext.request.contextPath}/index.jsp">Log in</a>
  </p>
</div>

<%--
    Shows only the fields that match the chosen role. This is a convenience;
    RegisterServlet checks the fields again on the server either way.
--%>
<script>
  function showRoleFields() {
    var role = document.getElementById("role").value;
    document.getElementById("joinTeam").style.display  = role === "Manager" ? "none" : "";
    document.getElementById("carNumber").style.display = role === "Driver"  ? "" : "none";
    document.getElementById("newTeam").style.display   = role === "Manager" ? "" : "none";
  }
  document.getElementById("role").addEventListener("change", showRoleFields);
  showRoleFields();
</script>

</body>
</html>
