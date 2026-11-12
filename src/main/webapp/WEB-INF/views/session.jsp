<%--
    One session: FR8 lap entry and FR7 tire usage.
    Rendered only by SessionDetailServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.SessionRow, cs157a.model.Lap,
                 cs157a.model.TireSet, cs157a.model.TireUsage, cs157a.model.User,
                 cs157a.dao.LapDAO, cs157a.util.Html" %>

<%
    SessionRow s = (SessionRow) request.getAttribute("session");

    @SuppressWarnings("unchecked")
    List<Lap> laps = (List<Lap>) request.getAttribute("laps");

    @SuppressWarnings("unchecked")
    List<TireSet> tireSets = (List<TireSet>) request.getAttribute("tireSets");

    @SuppressWarnings("unchecked")
    List<TireUsage> usages = (List<TireUsage>) request.getAttribute("usages");

    LapDAO.LapStats stats = (LapDAO.LapStats) request.getAttribute("stats");

    String notice = (String) request.getAttribute("notice");
    String error  = (String) request.getAttribute("error");
    User   user   = (User) session.getAttribute("user");
    String ctx    = request.getContextPath();
%>

<html>
<head>
  <title>Pitwall - Session <%= s.getSessionId() %></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1><%= Html.escape(s.getSessionType()) %> at <%= Html.escape(s.getTrackName()) %></h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <a href="<%= ctx %>/app/sessions">Sessions</a>
    <a href="<%= ctx %>/app/setups">Setup search</a>
    <% if ("Manager".equals(user.getRole())) { %>
      <a href="<%= ctx %>/app/events">Events</a>
      <a href="<%= ctx %>/app/tires">Tire sets</a>
    <% } %>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>

  <p>
    <%= Html.escape(s.getEventName()) %> &nbsp;|&nbsp;
    <%= s.getSessionDate() %> &nbsp;|&nbsp;
    Car <%= Html.escape(s.getChassisNo()) %> &nbsp;|&nbsp;
    Driver <%= Html.escape(s.getDriverName()) %> &nbsp;|&nbsp;
    Setup <%= s.getSetupName() == null ? "not recorded" : Html.escape(s.getSetupName()) %>
    <% if (s.getWeather() != null) { %>
      &nbsp;|&nbsp; <%= Html.escape(s.getWeather()) %>
    <% } %>
    <% if (s.getAirTempC() != null) { %>
      &nbsp;|&nbsp; <%= s.getAirTempC() %> C
    <% } %>
  </p>

  <% if (notice != null) { %>
    <p class="hint"><%= Html.escape(notice) %></p>
  <% } %>
  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } %>
</div>


<%-- ------------------------------- FR8: laps ------------------------------ --%>
<div class="panel">
  <h2>Lap times</h2>

  <% if (laps.isEmpty()) { %>
    <p class="hint">No laps entered for this session yet.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Lap</th>
        <th>Time</th>
        <th>Seconds</th>
        <th>Counts</th>
      </tr>
      <% for (Lap l : laps) { %>
        <tr>
          <td><%= l.getLapNumber() %></td>
          <td><%= l.getFormatted() %></td>
          <td><%= l.getLapTimeSec() %></td>
          <td><%= l.isValid() ? "yes" : "no, ran wide" %></td>
        </tr>
      <% } %>
    </table>

    <% if (stats != null) { %>
      <p>
        <strong><%= stats.count %></strong> valid lap(s) &nbsp;|&nbsp;
        best <strong><%= stats.getBestFormatted() %></strong> &nbsp;|&nbsp;
        average <strong><%= stats.getAverageFormatted() %></strong>
      </p>
      <p class="hint">
        Invalid laps are left out of the best and the average, so a lap where
        the car ran wide cannot look like the fastest lap of the session.
      </p>
    <% } else { %>
      <p class="hint">None of these laps counts, so there is no best lap yet.</p>
    <% } %>
  <% } %>

  <h3>Add lap times</h3>

  <form method="post" action="<%= ctx %>/app/session">
    <input type="hidden" name="sessionId" value="<%= s.getSessionId() %>">
    <input type="hidden" name="action" value="laps">
    <p>
      <label for="lapTimes">One lap per line</label><br>
      <textarea id="lapTimes" name="lapTimes" rows="6" cols="30"
                placeholder="1:41.482&#10;101.330&#10;1:40.998 x"></textarea>
    </p>
    <p>
      <button type="submit">Save laps</button>
    </p>
  </form>

  <p class="hint">
    Either form works: <code>101.482</code> or <code>1:41.482</code>.
    Put an <code>x</code> after a time to mark the lap invalid.
    Lap numbers carry on from the laps already saved, and the whole batch is
    written in one transaction.
  </p>
</div>


<%-- ---------------------------- FR7: tire usage --------------------------- --%>
<div class="panel">
  <h2>Tire usage</h2>

  <p class="hint">
    Recording a set against this session counts as one heat cycle for that
    set. When a set reaches its maximum it is retired automatically.
  </p>

  <%
     int usedHere = 0;
     for (TireUsage u : usages) { if (u.getSessionId() == s.getSessionId()) { usedHere++; } }
  %>

  <% if (usedHere == 0) { %>
    <p class="hint">No tire set recorded against this session yet.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Set no</th>
        <th>Compound</th>
        <th>Laps run</th>
        <th>Heat cycles</th>
        <th>Remaining</th>
        <th>Status</th>
      </tr>
      <% for (TireUsage u : usages) {
           if (u.getSessionId() != s.getSessionId()) { continue; } %>
        <tr>
          <td><%= u.getSetNo() %></td>
          <td><%= Html.escape(u.getCompound()) %></td>
          <td><%= u.getLapsRun() %></td>
          <td><%= u.getHeatCycles() %> / <%= u.getMaxHeatCycles() %></td>
          <td><%= u.getRemainingCycles() %></td>
          <td><%= Html.escape(u.getStatus()) %></td>
        </tr>
      <% } %>
    </table>
  <% } %>

  <h3>Record a tire set</h3>

  <% if (tireSets.isEmpty()) { %>
    <p class="hint">
      Your team has no tire sets in service. A manager adds them on the Tire
      sets page.
    </p>
  <% } else { %>
    <form method="post" action="<%= ctx %>/app/session">
      <input type="hidden" name="sessionId" value="<%= s.getSessionId() %>">
      <input type="hidden" name="action" value="tires">
      <p>
        <label for="setNo">Tire set</label><br>
        <select id="setNo" name="setNo" required>
          <% for (TireSet t : tireSets) { %>
            <option value="<%= t.getSetNo() %>">
              Set <%= t.getSetNo() %> - <%= Html.escape(t.getCompound()) %>
              (<%= t.getHeatCycles() %>/<%= t.getMaxHeatCycles() %> cycles used)
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="lapsRun">Laps run on this set</label><br>
        <input type="number" id="lapsRun" name="lapsRun" min="0" max="999" value="0" required>
      </p>
      <p>
        <button type="submit">Record usage</button>
      </p>
    </form>
  <% } %>
</div>


<%-- --------------------------- driver feedback ---------------------------- --%>
<div class="panel">
  <h2>Driver feedback</h2>

  <form method="post" action="<%= ctx %>/app/session">
    <input type="hidden" name="sessionId" value="<%= s.getSessionId() %>">
    <input type="hidden" name="action" value="feedback">
    <p>
      <textarea name="driverFeedback" rows="3" cols="60"><%= s.getDriverFeedback() == null ? "" : Html.escape(s.getDriverFeedback()) %></textarea>
    </p>
    <p>
      <button type="submit">Save feedback</button>
    </p>
  </form>
</div>

</body>
</html>
