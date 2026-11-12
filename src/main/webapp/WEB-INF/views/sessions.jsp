<%--
    FR6 - the session list and the form that records a new one.
    Rendered only by SessionServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.SessionRow, cs157a.model.Event,
                 cs157a.model.Car, cs157a.model.CarSetup, cs157a.model.DriverOption,
                 cs157a.model.User, cs157a.util.Html" %>

<%
    @SuppressWarnings("unchecked")
    List<SessionRow> sessions = (List<SessionRow>) request.getAttribute("sessions");

    @SuppressWarnings("unchecked")
    List<Event> events = (List<Event>) request.getAttribute("events");

    @SuppressWarnings("unchecked")
    List<Car> cars = (List<Car>) request.getAttribute("cars");

    @SuppressWarnings("unchecked")
    List<DriverOption> drivers = (List<DriverOption>) request.getAttribute("drivers");

    @SuppressWarnings("unchecked")
    List<CarSetup> setups = (List<CarSetup>) request.getAttribute("setups");

    String error = (String) request.getAttribute("error");
    String added = request.getParameter("added");
    User   user  = (User) session.getAttribute("user");
    String ctx   = request.getContextPath();
%>

<html>
<head>
  <title>Pitwall - Sessions</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Sessions</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <% if ("Manager".equals(user.getRole())) { %>
      <a href="<%= ctx %>/app/roster">Roster</a>
      <a href="<%= ctx %>/app/cars">Cars</a>
      <a href="<%= ctx %>/app/tires">Tire sets</a>
      <a href="<%= ctx %>/app/events">Events</a>
    <% } %>
    <a href="<%= ctx %>/app/sessions">Sessions</a>
    <a href="<%= ctx %>/app/setups">Setup search</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>

  <p class="hint">
    A session is one time the car goes out on track. Open a session to enter
    its lap times and record which tire set it ran.
  </p>
</div>

<div class="panel">
  <h2><%= sessions.size() %> session(s)</h2>

  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } else if (added != null) { %>
    <p class="hint">Session added. Open it to enter lap times.</p>
  <% } %>

  <% if (sessions.isEmpty()) { %>
    <p class="hint">No sessions recorded yet. Add the first one below.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Date</th>
        <th>Event</th>
        <th>Track</th>
        <th>Type</th>
        <th>Car</th>
        <th>Driver</th>
        <th>Setup</th>
        <th>Weather</th>
        <th>Laps</th>
        <th>Best lap</th>
        <th></th>
      </tr>

      <% for (SessionRow s : sessions) { %>
        <tr>
          <td><%= s.getSessionDate() %></td>
          <td><%= Html.escape(s.getEventName()) %></td>
          <td><%= Html.escape(s.getTrackName()) %></td>
          <td><%= Html.escape(s.getSessionType()) %></td>
          <td><%= Html.escape(s.getChassisNo()) %></td>
          <td><%= Html.escape(s.getDriverName()) %></td>
          <td><%= s.getSetupName() == null ? "-" : Html.escape(s.getSetupName()) %></td>
          <td><%= Html.escape(s.getWeather()) %></td>
          <td><%= s.getLapCount() %></td>
          <td><%= s.getBestLapFormatted() %></td>
          <td><a href="<%= ctx %>/app/session?sessionId=<%= s.getSessionId() %>">Open</a></td>
        </tr>
      <% } %>
    </table>
  <% } %>
</div>

<div class="panel">
  <h2>Record a session</h2>

  <% if (events.isEmpty()) { %>
    <p class="hint">
      Your team has no events yet. A manager has to plan one first on the
      Events page.
    </p>
  <% } else if (drivers.isEmpty() || cars.isEmpty()) { %>
    <p class="hint">
      Your team needs at least one car and one driver before a session can be
      recorded.
    </p>
  <% } else { %>
    <form method="post" action="<%= ctx %>/app/sessions">
      <p>
        <label for="eventId">Event</label><br>
        <select id="eventId" name="eventId" required>
          <option value="">-- choose an event --</option>
          <% for (Event e : events) { %>
            <option value="<%= e.getEventId() %>">
              <%= Html.escape(e.getEventName()) %> (<%= Html.escape(e.getTrackName()) %>)
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="carId">Car</label><br>
        <select id="carId" name="carId" required>
          <% for (Car c : cars) { %>
            <option value="<%= c.getCarId() %>">
              <%= Html.escape(c.getChassisNo()) %> - <%= Html.escape(c.getModel()) %>
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="driverUserId">Driver</label><br>
        <select id="driverUserId" name="driverUserId" required>
          <% for (DriverOption d : drivers) { %>
            <option value="<%= d.getUserId() %>">
              #<%= d.getDriverNumber() %> <%= Html.escape(d.getDisplayName()) %>
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="setupId">Setup</label><br>
        <select id="setupId" name="setupId">
          <option value="">-- not written down yet --</option>
          <% for (CarSetup cs : setups) { %>
            <option value="<%= cs.getSetupId() %>">
              <%= Html.escape(cs.getSetupName()) %> (<%= Html.escape(cs.getChassisNo()) %>)
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="sessionType">Session type</label><br>
        <select id="sessionType" name="sessionType" required>
          <option value="Practice">Practice</option>
          <option value="Qualifying">Qualifying</option>
          <option value="Race">Race</option>
          <option value="Test">Test</option>
        </select>
      </p>
      <p>
        <label for="sessionDate">Date</label><br>
        <input type="date" id="sessionDate" name="sessionDate" required>
      </p>
      <p>
        <label for="weather">Weather</label><br>
        <input type="text" id="weather" name="weather" size="24" maxlength="50"
               placeholder="Clear, Light Rain, Overcast">
      </p>
      <p>
        <label for="airTempC">Air temperature in C</label><br>
        <input type="text" id="airTempC" name="airTempC" size="10" placeholder="21.5">
      </p>
      <p>
        <label for="driverFeedback">Driver feedback</label><br>
        <textarea id="driverFeedback" name="driverFeedback" rows="3" cols="48"
                  placeholder="Can be filled in after the session"></textarea>
      </p>
      <p>
        <button type="submit">Record session</button>
      </p>
    </form>

    <p class="hint">
      The setup may be left empty. A session is often recorded before anybody
      has written the setup sheet down, which is why setup_id allows no value.
    </p>
  <% } %>
</div>

</body>
</html>
