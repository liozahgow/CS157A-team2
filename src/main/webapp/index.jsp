<%--
    Step 1 of the login flow: the login form.

    This is the only JSP outside WEB-INF, because a visitor who has not logged
    in yet still has to be able to reach it.

    The form posts to /login, which is handled by LoginServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
<head>
  <title>Pitwall - Sign in</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Pitwall</h1>
  <p>CS157A Team 2 - racing team data platform</p>

  <form method="post" action="${pageContext.request.contextPath}/login">
    <p>
      <label for="email">Email</label><br>
      <input type="text" id="email" name="email" size="34" required>
    </p>
    <p>
      <label for="password">Password</label><br>
      <input type="password" id="password" name="password" size="34" required>
    </p>
    <p>
      <button type="submit">Log in</button>
    </p>
  </form>

  <%-- LoginServlet redirects back here with ?error=... when something failed. --%>
  <% String error = request.getParameter("error"); %>
  <% if ("bad".equals(error)) { %>
      <p class="error">Wrong email or password.</p>
  <% } else if ("timeout".equals(error)) { %>
      <p class="error">Please log in first.</p>
  <% } %>

  <%-- RegisterServlet redirects here with ?registered=... after a sign-up. --%>
  <% String registered = request.getParameter("registered"); %>
  <% if ("ok".equals(registered)) { %>
      <p class="hint">Account created. You can log in now.</p>
  <% } else if ("pending".equals(registered)) { %>
      <p class="hint">Account created. Your new team is waiting for administrator approval.</p>
  <% } %>

  <p>
    New to Pitwall? <a href="${pageContext.request.contextPath}/register">Create an account</a>
  </p>

  <p class="hint">
    Test account: <code>manager@apex.example</code> / <code>manager123</code>
    (see db/README.md for the rest)
  </p>
</div>

</body>
</html>
