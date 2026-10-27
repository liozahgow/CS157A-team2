<%--
    FR2 - the profile page. Rendered only by ProfileServlet.

    "profile" is the user freshly loaded from the database. After a failed
    POST the servlet also sets "error", and the editable fields show what was
    submitted instead, so the user does not lose their typing.

    Everything that came from a user goes through Html.escape before it is printed.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="cs157a.model.User, cs157a.util.Html" %>

<%
    User    profile  = (User) request.getAttribute("profile");
    String  error    = (String) request.getAttribute("error");
    boolean saved    = "1".equals(request.getParameter("saved"));
    boolean isDriver = "Driver".equals(profile.getRole());

    // A failed POST shows the submitted values; otherwise the stored ones.
    boolean refill = error != null;
    String displayName  = refill ? request.getParameter("displayName") : profile.getDisplayName();
    String phone        = refill ? request.getParameter("phone")       : profile.getPhone();
    String driverNumber = refill ? request.getParameter("driverNumber")
                        : (profile.getDriverNumber() == null ? "" : profile.getDriverNumber().toString());
%>

<html>
<head>
  <title>Pitwall - My profile</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>My profile</h1>

  <p>
    <a href="${pageContext.request.contextPath}/app/home">Back to home</a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/logout">Log out</a>
  </p>

  <% if (error != null) { %>
      <p class="error"><%= Html.escape(error) %></p>
  <% } else if (saved) { %>
      <p class="hint">Your profile has been updated.</p>
  <% } %>

  <%-- Shown, not editable: FR2 does not let users change these. --%>
  <p>
    Email: <%= Html.escape(profile.getEmail()) %><br>
    Role: <%= Html.escape(profile.getRole()) %>
  </p>

  <form method="post" action="${pageContext.request.contextPath}/app/profile">
    <p>
      <label for="displayName">Display name</label><br>
      <input type="text" id="displayName" name="displayName" size="34" maxlength="100" required
             value="<%= Html.escape(displayName) %>">
    </p>
    <p>
      <label for="phone">Phone (optional)</label><br>
      <input type="text" id="phone" name="phone" size="34" maxlength="20"
             value="<%= Html.escape(phone) %>">
    </p>

    <% if (isDriver) { %>
    <p>
      <label for="driverNumber">Car number (0-999)</label><br>
      <input type="number" id="driverNumber" name="driverNumber" min="0" max="999" required
             value="<%= Html.escape(driverNumber) %>">
    </p>
    <% } %>

    <h2>Change password</h2>
    <p class="hint">Leave both boxes empty to keep your current password.</p>
    <p>
      <label for="newPassword">New password (at least 8 characters)</label><br>
      <input type="password" id="newPassword" name="newPassword" size="34" minlength="8">
    </p>
    <p>
      <label for="confirmPassword">Repeat new password</label><br>
      <input type="password" id="confirmPassword" name="confirmPassword" size="34">
    </p>

    <%-- Needed for every change, not just a new password (FR2). --%>
    <p>
      <label for="currentPassword">Current password</label><br>
      <input type="password" id="currentPassword" name="currentPassword" size="34" required>
    </p>

    <p>
      <button type="submit">Save changes</button>
    </p>
  </form>
</div>

</body>
</html>
