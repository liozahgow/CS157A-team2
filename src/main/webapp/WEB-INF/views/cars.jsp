<%--
    FR4 - the cars a team owns. Rendered only by CarServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.Car, cs157a.util.Html" %>

<%
    @SuppressWarnings("unchecked")
    List<Car> cars = (List<Car>) request.getAttribute("cars");

    boolean includeRetired = (Boolean) request.getAttribute("includeRetired");
    String  error          = (String)  request.getAttribute("error");
    String  added          = request.getParameter("added");
    String  ctx            = request.getContextPath();
%>

<html>
<head>
  <title>Pitwall - Cars</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Cars</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <a href="<%= ctx %>/app/roster">Roster</a>
    <a href="<%= ctx %>/app/cars">Cars</a>
    <a href="<%= ctx %>/app/tires">Tire sets</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>
</div>

<div class="panel">
  <h2><%= cars.size() %> car(s)</h2>

  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } else if (added != null) { %>
    <p class="hint">Car added.</p>
  <% } %>

  <p>
    <% if (includeRetired) { %>
      <a href="<%= ctx %>/app/cars">Hide retired cars</a>
    <% } else { %>
      <a href="<%= ctx %>/app/cars?retired=1">Show retired cars</a>
    <% } %>
  </p>

  <% if (cars.isEmpty()) { %>
    <p class="hint">No cars yet. Add the first one below.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Chassis no</th>
        <th>Model</th>
        <th>Class</th>
        <th>Status</th>
        <th></th>
      </tr>

      <% for (Car c : cars) { %>
        <tr>
          <td><%= Html.escape(c.getChassisNo()) %></td>
          <td><%= Html.escape(c.getModel()) %></td>
          <td><%= Html.escape(c.getCarClass()) %></td>
          <td><%= c.isActive() ? "In service" : "Retired" %></td>
          <td>
            <form method="post" action="<%= ctx %>/app/cars">
              <input type="hidden" name="carId" value="<%= c.getCarId() %>">
              <input type="hidden" name="action" value="<%= c.isActive() ? "retire" : "reactivate" %>">
              <button type="submit"><%= c.isActive() ? "Retire" : "Put back in service" %></button>
            </form>
          </td>
        </tr>
      <% } %>
    </table>
  <% } %>
</div>

<div class="panel">
  <h2>Add a car</h2>

  <form method="post" action="<%= ctx %>/app/cars">
    <p>
      <label for="chassisNo">Chassis number</label><br>
      <input type="text" id="chassisNo" name="chassisNo" size="30" maxlength="50" required>
    </p>
    <p>
      <label for="model">Model</label><br>
      <input type="text" id="model" name="model" size="30" maxlength="100" required
             placeholder="Mazda MX-5 NC">
    </p>
    <p>
      <label for="carClass">Class</label><br>
      <input type="text" id="carClass" name="carClass" size="30" maxlength="50" required
             placeholder="Spec Miata">
    </p>
    <p>
      <button type="submit">Add car</button>
    </p>
  </form>

  <p class="hint">
    A car is never deleted. Retiring it keeps the old sessions and lap times
    readable.
  </p>
</div>

</body>
</html>
