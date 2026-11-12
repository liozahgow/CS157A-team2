<%--
    FR5 - the team calendar. Rendered only by EventServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.Event, cs157a.model.SessionSummary,
                 cs157a.model.Track, cs157a.util.Html" %>

<%
    @SuppressWarnings("unchecked")
    List<Event> events = (List<Event>) request.getAttribute("events");

    @SuppressWarnings("unchecked")
    List<Track> tracks = (List<Track>) request.getAttribute("tracks");

    @SuppressWarnings("unchecked")
    List<SessionSummary> sessions = (List<SessionSummary>) request.getAttribute("sessions");

    Event  selected = (Event)  request.getAttribute("selectedEvent");
    String error    = (String) request.getAttribute("error");
    String added    = request.getParameter("added");
    String ctx      = request.getContextPath();
%>

<html>
<head>
  <title>Pitwall - Events</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Team calendar</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <a href="<%= ctx %>/app/roster">Roster</a>
    <a href="<%= ctx %>/app/cars">Cars</a>
    <a href="<%= ctx %>/app/tires">Tire sets</a>
    <a href="<%= ctx %>/app/events">Events</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>
</div>

<div class="panel">
  <h2><%= events.size() %> event(s)</h2>

  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } else if (added != null) { %>
    <p class="hint">Event added.</p>
  <% } %>

  <% if (events.isEmpty()) { %>
    <p class="hint">No events planned yet. Add the first one below.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Event</th>
        <th>Track</th>
        <th>Start</th>
        <th>End</th>
        <th>Type</th>
        <th></th>
      </tr>

      <% for (Event e : events) { %>
        <tr>
          <td><%= Html.escape(e.getEventName()) %></td>
          <td><%= Html.escape(e.getTrackName()) %></td>
          <td><%= e.getStartDate() %></td>
          <td><%= e.getEndDate() %></td>
          <td><%= Html.escape(e.getEventType()) %></td>
          <td><a href="<%= ctx %>/app/events?eventId=<%= e.getEventId() %>">Sessions</a></td>
        </tr>
      <% } %>
    </table>
  <% } %>
</div>

<% if (selected != null) { %>
<div class="panel">
  <h2>Sessions at <%= Html.escape(selected.getEventName()) %></h2>

  <p class="hint">
    <%= Html.escape(selected.getTrackName()) %>,
    <%= selected.getStartDate() %> to <%= selected.getEndDate() %>
  </p>

  <% if (sessions.isEmpty()) { %>
    <p class="hint">No sessions have been recorded at this event yet.</p>
  <% } else { %>
    <table>
      <tr>
        <th>Date</th>
        <th>Type</th>
        <th>Driver</th>
        <th>Car</th>
        <th>Weather</th>
      </tr>

      <% for (SessionSummary s : sessions) { %>
        <tr>
          <td><%= s.getSessionDate() %></td>
          <td><%= Html.escape(s.getSessionType()) %></td>
          <td><%= Html.escape(s.getDriverName()) %></td>
          <td><%= Html.escape(s.getChassisNo()) %></td>
          <td><%= Html.escape(s.getWeather()) %></td>
        </tr>
      <% } %>
    </table>
  <% } %>
</div>
<% } %>

<div class="panel">
  <h2>Plan an event</h2>

  <form method="post" action="<%= ctx %>/app/events">
    <p>
      <label for="eventName">Event name</label><br>
      <input type="text" id="eventName" name="eventName" size="36" maxlength="100" required
             placeholder="Laguna Seca Spring Test">
    </p>
    <p>
      <label for="trackId">Track</label><br>
      <select id="trackId" name="trackId" required>
        <option value="">-- choose a track --</option>
        <% for (Track t : tracks) { %>
          <option value="<%= t.getTrackId() %>">
            <%= Html.escape(t.getTrackName()) %> (<%= Html.escape(t.getLocation()) %>)
          </option>
        <% } %>
      </select>
    </p>
    <p>
      <label for="startDate">Start date</label><br>
      <input type="date" id="startDate" name="startDate" required>
    </p>
    <p>
      <label for="endDate">End date</label><br>
      <input type="date" id="endDate" name="endDate" required>
    </p>
    <p>
      <label for="eventType">Type</label><br>
      <select id="eventType" name="eventType">
        <option value="Race Weekend">Race Weekend</option>
        <option value="Test Day">Test Day</option>
      </select>
    </p>
    <p>
      <button type="submit">Add event</button>
    </p>
  </form>
</div>

</body>
</html>
