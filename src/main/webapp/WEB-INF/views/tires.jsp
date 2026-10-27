<%--
    FR4 - the tire inventory. Rendered only by TireSetServlet.

    tire_sets is a weak entity: the set number is only unique inside the team,
    so the table shows "Set 1", "Set 2" and so on per team, not a global id.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.TireSet, cs157a.util.Html" %>

<%
    @SuppressWarnings("unchecked")
    List<TireSet> sets = (List<TireSet>) request.getAttribute("sets");

    boolean includeRetired = (Boolean) request.getAttribute("includeRetired");
    String  error          = (String)  request.getAttribute("error");
    String  added          = request.getParameter("added");
    String  ctx            = request.getContextPath();
%>

<html>
<head>
  <title>Pitwall - Tire sets</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Tire sets</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <a href="<%= ctx %>/app/roster">Roster</a>
    <a href="<%= ctx %>/app/cars">Cars</a>
    <a href="<%= ctx %>/app/tires">Tire sets</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>
</div>

<div class="panel">
  <h2><%= sets.size() %> set(s)</h2>

  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } else if (added != null) { %>
    <p class="hint">Added as set number <%= Html.escape(added) %>.</p>
  <% } %>

  <p>
    <% if (includeRetired) { %>
      <a href="<%= ctx %>/app/tires">Hide retired sets</a>
    <% } else { %>
      <a href="<%= ctx %>/app/tires?retired=1">Show retired sets</a>
    <% } %>
  </p>

  <% if (sets.isEmpty()) { %>
    <p class="hint">No tire sets yet. Add the first one below.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Set no</th>
        <th>Compound</th>
        <th>Size</th>
        <th>Received</th>
        <th>Heat cycles</th>
        <th>Remaining</th>
        <th>Status</th>
        <th></th>
      </tr>

      <% for (TireSet t : sets) { %>
        <tr>
          <td><%= t.getSetNo() %></td>
          <td><%= Html.escape(t.getCompound()) %></td>
          <td><%= Html.escape(t.getTireSize()) %></td>
          <td><%= t.getReceivedDate() == null ? "" : t.getReceivedDate().toString() %></td>
          <td><%= t.getHeatCycles() %> / <%= t.getMaxHeatCycles() %></td>
          <td>
            <%= t.getRemainingCycles() %>
            <% if (t.isNearLimit()) { %>
              <span class="error">&nbsp;one cycle left</span>
            <% } %>
          </td>
          <td><%= Html.escape(t.getStatus()) %></td>
          <td>
            <form method="post" action="<%= ctx %>/app/tires">
              <input type="hidden" name="setNo" value="<%= t.getSetNo() %>">
              <% boolean retired = "Retired".equals(t.getStatus()); %>
              <input type="hidden" name="action" value="<%= retired ? "reactivate" : "retire" %>">
              <button type="submit"><%= retired ? "Put back in use" : "Retire" %></button>
            </form>
          </td>
        </tr>
      <% } %>
    </table>
  <% } %>
</div>

<div class="panel">
  <h2>Add a tire set</h2>

  <form method="post" action="<%= ctx %>/app/tires">
    <p>
      <label for="compound">Compound</label><br>
      <select id="compound" name="compound" required>
        <option value="">-- choose --</option>
        <option value="Soft">Soft</option>
        <option value="Medium">Medium</option>
        <option value="Hard">Hard</option>
        <option value="Wet">Wet</option>
      </select>
    </p>
    <p>
      <label for="tireSize">Size</label><br>
      <input type="text" id="tireSize" name="tireSize" size="24" maxlength="50"
             placeholder="205/50R15">
    </p>
    <p>
      <label for="receivedDate">Received date</label><br>
      <input type="date" id="receivedDate" name="receivedDate">
    </p>
    <p>
      <label for="maxHeatCycles">Maximum heat cycles</label><br>
      <input type="number" id="maxHeatCycles" name="maxHeatCycles"
             min="1" max="99" value="8" required>
    </p>
    <p>
      <button type="submit">Add tire set</button>
    </p>
  </form>

  <p class="hint">
    The set number is assigned automatically: a team numbers its own sets from
    1 upwards, and the number only has to be unique inside the team.
    A new set starts at zero heat cycles.
  </p>
</div>

</body>
</html>
